package com.hktcode.ruoshui.reciever.pgsql.upper.storeman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.ruoshui.Ruoshui;
import com.hktcode.ruoshui.reciever.pgsql.exception.*;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperConfig;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperEntity;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Repository
public class UpperKeeperOnlyone
{
    private static final Logger logger = LoggerFactory.getLogger(UpperKeeperOnlyone.class);

    private final YAMLMapper mapper;

    private final ImmutableMap<String, UpperConfig> etcval = ImmutableMap.of();

    public UpperKeeperOnlyone(@Autowired YAMLMapper mapper)
    {
        this.mapper = mapper;
    }

    public UpperResult put(UpperEntity entity, long deletets)
    {
        String name = entity.fullname;
        String yaml = this.toYamlString(entity);
        // 检查文件 .upsert.yml .delete.yml
        // - - if .delete.yml exists: delete delete.yml
        // 先写del
        // 重命名del为yml
        updertConfFile(name, "yml", yaml);
        deleteConfFile(name, "del");
        return UpperResult.of(entity, deletets);
    }

    public UpperResult del(UpperEntity entity, long deletets)
    {
        String name = entity.fullname;
        String yaml = this.toYamlString(entity);
        // 重命名yml为del
        // 判断是否删除del
        if (this.etcval.containsKey(name)) {
            updertConfFile(name, "del", yaml);
        }
        else {
            deleteConfFile(name, "del");
        }
        deleteConfFile(name, "yml");
        if (deletets == Long.MAX_VALUE) {
            deletets = System.currentTimeMillis();
        }
        return UpperResult.of(entity, deletets);
    }

    public UpperResult pst(UpperEntity entity, JsonNode body)
    {
        String name = entity.fullname;
        UpperResult result = entity.pst(body);
        String yaml = this.toYamlString(entity);
        // 先写del
        // 重命名del为yml
        deleteConfFile(name, "del");
        updertConfFile(name, "yml", yaml);
        return result;
    }

    private String toYamlString(UpperEntity entity)
    {
        ObjectNode result = this.mapper.getNodeFactory().objectNode();
        result.put("fullname", entity.fullname);
        ObjectNode consumer = result.putObject("consumer");
        ObjectNode srcqueue = result.putObject("srcqueue");
        ObjectNode junction = result.putObject("junction");
        ObjectNode tgtqueue = result.putObject("tgtqueue");
        ObjectNode producer = result.putObject("producer");
        entity.consumer.config.toJsonObject(consumer);
        entity.srcqueue.config.toJsonObject(srcqueue);
        entity.junction.config.toJsonObject(junction);
        entity.tgtqueue.config.toJsonObject(tgtqueue);
        entity.producer.config.toJsonObject(producer);
        try {
            return this.mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new NeverHappenAssertionError(e);
        }
    }

    private static Path getPath(String name, String fext)
    {
        return Paths.get("var", "auto", "recievers.pgsql.upper", name + "." + fext);
    }

    private static void deleteConfFile(String name, String fext)
    {
        Path path = getPath(name, fext);
        try {
            Files.deleteIfExists(Paths.get(Ruoshui.HOME, path.toString()));
        } catch (FileSystemLoopException ex) {
            logger.error("file system loop when deleting file: path={}", path, ex);
            throw new ConfFileFileSystemLoopException(name, path.toString(), ex);
        } catch (DirectoryNotEmptyException ex) {
            logger.error(".del file is a directory and not empty: path={}", path, ex);
            throw new ConfFileIsNotEmptyDirectoryException(name, path.toString(), ex);
        } catch (AccessDeniedException ex) {
            logger.error("delete file access denied: path={}", path, ex);
            throw new ConfFileAccessDeniedException(name, path.toString(), ex);
        } catch (FileSystemException ex) {
            // 不可能抛出以下异常，因此统一当作FileSystemException处理
            // NotSuchFileException
            // NotDirectoryException
            // NotLinkException
            // AtomicMoveNotSupportedException
            // FileAlreadyExistsException
            // 如果上级目录不是一个目录（例如：$RUOSHUI_HOME/var是一个文件）
            // 会抛出这个FileSystemException，并带有消息：.../var: 不是目录
            logger.error("file system error when deleting file: path={}", path, ex);
            throw new ConfFileFileSystemException(name, path.toString(), ConfFileIOException.CODE, ex);
        } catch (IOException ex) {
            logger.error("io error when deleting file: path={}", path, ex);
            throw new ConfFileIOException(name, path.toString(), ConfFileIOException.CODE, ex);
        }
    }

    private static void updertConfFile(String name, String fext, String yaml)
    {
        Path path = getPath(name, fext);
        Path full = Paths.get(Ruoshui.HOME, path.toString());
        OpenOption[] opts = new OpenOption[] {
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE
        };
        try {
            Files.createDirectories(full.getParent().toAbsolutePath());
            try (FileChannel channel = FileChannel.open(full, opts)) {
                try (FileLock lock = channel.tryLock()) {
                    if (lock == null) {
                        logger.warn("lock file fail: path={}", path);
                        throw new ConfFileLockFailureException(name, path.toString());
                    }
                    channel.write(ByteBuffer.wrap(yaml.getBytes(StandardCharsets.UTF_8)));
                }
            }
        } catch (FileSystemLoopException ex) {
            logger.error("file system loop when deleting file: path={}", path, ex);
            throw new ConfFileFileSystemLoopException(name, path.toString(), ex);
        } catch (AccessDeniedException ex) {
            logger.error("delete file access denied: path={}", path, ex);
            throw new ConfFileAccessDeniedException(name, path.toString(), ex);
        } catch (FileSystemException ex) {
            // 不可能抛出以下异常，因此统一当作FileSystemException处理
            // NotSuchFileException
            // NotDirectoryException
            // NotLinkException
            // AtomicMoveNotSupportedException
            // DirectoryNotEmptyException
            // FileAlreadyExistsException
            // 如果上级目录不是一个目录（例如：$RUOSHUI_HOME/var是一个文件）
            // 会抛出这个FileSystemException，并带有消息：.../var: 不是目录
            logger.error("file system error when deleting file: path={}", path, ex);
            throw new ConfFileFileSystemException(name, path.toString(), ConfFileFileSystemException.CODE, ex);
        } catch (IOException ex) {
            logger.error("io error when deleting file: path={}", path, ex);
            throw new ConfFileIOException(name, path.toString(), ConfFileIOException.CODE, ex);
        }
    }
}
