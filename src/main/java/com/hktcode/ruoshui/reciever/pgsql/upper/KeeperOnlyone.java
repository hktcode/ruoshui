package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.ruoshui.Ruoshui;
import com.hktcode.ruoshui.reciever.pgsql.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Repository("upperKeeper")
public class KeeperOnlyone
{
    public final YAMLMapper mapper;

    private final ImmutableMap<String, Entity> etcval = ImmutableMap.of();

    public KeeperOnlyone(@Autowired YAMLMapper mapper)
    {
        this.mapper = mapper;
    }

    public void deleteYml(Entity.Result argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        final String name = argval.fullname;
        ObjectNode node = mapper.createObjectNode();
        node = argval.toJsonObject(node);
        String yaml = this.toYamlString(node);
        if (this.etcval.containsKey(name)) {
            updertConfFile(name, "tmp", yaml);
            renameConfFile(name, "tmp", "yml");
            renameConfFile(name, "yml", "del");
        }
        else {
            deleteConfFile(name, "yml");
            deleteConfFile(name, "tmp");
            deleteConfFile(name, "del");
        }
    }

    public void updertYml(Entity.Result argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        final String name = argval.fullname;
        ObjectNode node = mapper.createObjectNode();
        node = argval.toJsonObject(node);
        String yaml = this.toYamlString(node);
        updertConfFile(name, "tmp", yaml);
        renameConfFile(name, "tmp", "yml");
        deleteConfFile(name, "del");
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
        String filename = name + "." + fext;
        return Paths.get("var", "auto", "recievers.pgsql.upper", filename);
    }

    private static void deleteConfFile(String name, String fext)
    {
        String path = getPath(name, fext).toString();
        try {
            Files.deleteIfExists(Paths.get(Ruoshui.HOME, path));
        } catch (FileSystemLoopException ex) {
            logger.error("file system loop: delete_path={}", path, ex);
            throw new ConfFileFileSystemLoopException(name, path, ex);
        } catch (DirectoryNotEmptyException ex) {
            logger.error("a none empty directory: delete_path={}", path, ex);
            throw new ConfFileIsNotEmptyDirectoryException(name, path, ex);
        } catch (AccessDeniedException ex) {
            logger.error("access denied: delete_path={}", path, ex);
            throw new ConfFileAccessDeniedException(name, path, ex);
        } catch (FileSystemException ex) {
            // 不可能抛出以下异常，因此统一当作FileSystemException处理
            // NotSuchFileException
            // NotDirectoryException
            // NotLinkException
            // AtomicMoveNotSupportedException
            // FileAlreadyExistsException
            // 如果上级目录不是一个目录（例如：$RUOSHUI_HOME/var是一个文件）
            // 会抛出这个FileSystemException，并带有消息：.../var: 不是目录
            logger.error("file system error: delete_path={}", path, ex);
            HttpStatus code = ConfFileIOException.CODE;
            throw new ConfFileFileSystemException(name, path, code, ex);
        } catch (IOException ex) {
            logger.error("io error: delete_path={}", path, ex);
            HttpStatus code = ConfFileIOException.CODE;
            throw new ConfFileIOException(name, path, code, ex);
        }
    }

    private static void updertConfFile(String name, String fext, String yaml)
    {
        String path = getPath(name, fext).toString();
        Path full = Paths.get(Ruoshui.HOME, path);
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
                        throw new ConfFileLockFailureException(name, path);
                    }
                    byte[] bytes = yaml.getBytes(StandardCharsets.UTF_8);
                    channel.write(ByteBuffer.wrap(bytes));
                }
            }
        } catch (FileSystemLoopException ex) {
            logger.error("file system loop: read_path={}", path, ex);
            throw new ConfFileFileSystemLoopException(name, path, ex);
        } catch (AccessDeniedException ex) {
            logger.error("access denied: read_path={}", path, ex);
            throw new ConfFileAccessDeniedException(name, path, ex);
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
            logger.error("file system error: read_path={}", path, ex);
            HttpStatus code = ConfFileFileSystemException.CODE;
            throw new ConfFileFileSystemException(name, path, code, ex);
        } catch (IOException ex) {
            logger.error("io error: read_path={}", path, ex);
            HttpStatus code = ConfFileIOException.CODE;
            throw new ConfFileIOException(name, path, code, ex);
        }
    }

    private static void renameConfFile(String name, String srct, String tgtt)
    {
        String source = getPath(name, srct).toString();
        String target = getPath(name, tgtt).toString();
        Path fullsrc = Paths.get(Ruoshui.HOME, source);
        Path fulltgt = Paths.get(Ruoshui.HOME, target);
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
            logger.error("file system loop: move={}->{}", source, target, ex);
            throw new ConfFileFileSystemLoopException(name, source, ex);
        } catch (AccessDeniedException ex) {
            logger.error("access denied: move={}->{}", source, target, ex);
            throw new ConfFileAccessDeniedException(name, source, ex);
        } catch (FileSystemException ex) {
            // 不可能抛出以下异常，因此统一当作FileSystemException处理
            // NotDirectoryException
            // NotLinkException
            // DirectoryNotEmptyException
            // FileAlreadyExistsException
            // 如果上级目录不是一个目录（例如：$RUOSHUI_HOME/var是一个文件）
            // 会抛出这个FileSystemException，并带有消息：.../var: 不是目录
            logger.error("file system error: move={}->{}", source, target, ex);
            HttpStatus code = ConfFileFileSystemException.CODE;
            throw new ConfFileFileSystemException(name, source, code, ex);
        } catch (IOException ex) {
            logger.error("io error: move={}->{}", source, target, ex);
            HttpStatus code = ConfFileIOException.CODE;
            throw new ConfFileIOException(name, source, code, ex);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(KeeperOnlyone.class);
}
