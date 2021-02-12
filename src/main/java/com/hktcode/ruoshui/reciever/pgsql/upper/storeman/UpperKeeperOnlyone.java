package com.hktcode.ruoshui.reciever.pgsql.upper.storeman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.ruoshui.Ruoshui;
import com.hktcode.ruoshui.reciever.pgsql.exception.*;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperConfig;
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

    public final YAMLMapper mapper;

    private final ImmutableMap<String, UpperConfig> etcval = ImmutableMap.of();

    public UpperKeeperOnlyone(@Autowired YAMLMapper mapper)
    {
        this.mapper = mapper;
    }

    public long deleteYml(String name, ObjectNode node, long deletets)
    {
        String yaml = this.toYamlString(node);
        updertConfFile(name, "yml", yaml);
        renameConfFile(name, "yml", "del");
        if (this.etcval.containsKey(name)) {
            deleteConfFile(name, "del");
        }
        if (deletets == Long.MAX_VALUE) {
            deletets = System.currentTimeMillis();
        }
        return deletets;
    }

    public void updertYml(String name, ObjectNode node)
    {
        String yaml = this.toYamlString(node);
        updertConfFile(name, "del", yaml);
        renameConfFile(name, "del", "yml");
    }

    private String toYamlString(ObjectNode node)
    {
        try {
            return this.mapper.writeValueAsString(node);
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

    private static void renameConfFile(String name, String srct, String tgtt)
    {
        Path source = getPath(name, srct);
        Path target = getPath(name, tgtt);
        Path fullsrc = Paths.get(Ruoshui.HOME, source.toString());
        Path fulltgt = Paths.get(Ruoshui.HOME, target.toString());
        CopyOption[] opts = new CopyOption[] {
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
        };
        try {
            Files.move(fullsrc, fulltgt, opts);
        } catch (AtomicMoveNotSupportedException ex) {
            logger.error("", ex); // TODO:
        } catch (NoSuchFileException ex) {
            // TODO:
        } catch (FileSystemLoopException ex) {
            logger.error("file system loop when move file: source={}, target={}", source, target, ex);
            throw new ConfFileFileSystemLoopException(name, source.toString(), ex);
        } catch (AccessDeniedException ex) {
            logger.error("delete file access denied: source={}, target={}", source, target, ex);
            throw new ConfFileAccessDeniedException(name, source.toString(), ex);
        } catch (FileSystemException ex) {
            // 不可能抛出以下异常，因此统一当作FileSystemException处理
            // NotDirectoryException
            // NotLinkException
            // DirectoryNotEmptyException
            // FileAlreadyExistsException
            // 如果上级目录不是一个目录（例如：$RUOSHUI_HOME/var是一个文件）
            // 会抛出这个FileSystemException，并带有消息：.../var: 不是目录
            logger.error("file system error when deleting file: source={}, target={}", source, target, ex);
            throw new ConfFileFileSystemException(name, source.toString(), ConfFileFileSystemException.CODE, ex);
        } catch (IOException ex) {
            logger.error("io error when move file: source={}, target={}", source, target, ex);
            throw new ConfFileIOException(name, source.toString(), ConfFileIOException.CODE, ex);
        }
    }
}
