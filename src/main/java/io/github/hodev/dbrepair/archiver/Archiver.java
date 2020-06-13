package io.github.hodev.dbrepair.archiver;

import io.github.hodev.dbrepair.RepairConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {
    private final static String[] SQL_FILES_EXT = new String[]{"sql"};
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

    public String perform(RepairConfig config) {
        Collection<File> sqlFiles = FileUtils.listFiles(
            new File(config.getTempDirectory()),
            SQL_FILES_EXT,
            false
        );

        File f = new File(config.getArchiveLocation(), String.format(
            "database-%s.zip",
            dateFormatter.format(new Date())
        ));

        try {
            ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(f));
            sqlFiles.forEach(file -> {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    zipStream.putNextEntry(new ZipEntry(file.getName()));
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipStream.write(bytes, 0, length);
                    }
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            zipStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f.getAbsolutePath();
    }
}
