import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.codehaus.groovy.runtime.DateGroovyMethods

import java.util.regex.Matcher
import java.util.regex.Pattern

String filepath = payload.getAbsolutePath()
def pathSep = File.separator
def bufferSize = 1024
def ts = DateGroovyMethods.format(new Date(), "yyyyMMddHHmmss")
String DATA_IN_FOLDER = "datain"
String ERROR_FOLDER = "errors"

def replaceGroup(String regex, String str, int replacementGroup, String replacement) {
    Pattern p = Pattern.compile(regex)
    Matcher m = p.matcher(str)
    if (m.find()) {
        return new StringBuilder(str)
                .replace(m.start(replacementGroup), m.end(replacementGroup), replacement)
                .toString()
    }
    return str
}

if (filepath.endsWith(".gz")) {
    GzipCompressorInputStream gzIn = null
    FileInputStream inputStream = null
    BufferedInputStream bufferedInputStream = null
    FileOutputStream out = null
    try {
        inputStream = new FileInputStream(payload)
        bufferedInputStream = new BufferedInputStream(inputStream)
        String targetPath = replaceGroup(".*/([^/]+)/[^/]+\$", filepath, 1, DATA_IN_FOLDER)
        String filename = targetPath.substring(0, targetPath.indexOf(".gz"))
        out = new FileOutputStream(filename)
        gzIn = new GzipCompressorInputStream(bufferedInputStream)
        final byte[] buffer = new byte[bufferSize]
        int n = 0
        while ((n = gzIn.read(buffer)) != -1) {
            out.write(buffer, 0, n)
        }
        boolean fileSuccessfullyDeleted = payload.delete()
    } catch (Exception e) {
        println e.getMessage()
        e.printStackTrace()
        String errorPath = replaceGroup(".*/([^/]+)/[^/]+\$", filepath, 1, ERROR_FOLDER)
        boolean fileMoved = payload.renameTo(errorPath)
    } finally {
        if (out != null) {
            out.flush()
            out.close()
        }
        if (gzIn != null) {
            gzIn.close()
        }
        if (bufferedInputStream != null) {
            bufferedInputStream.close()
        }
        if (inputStream != null) {
            inputStream.close()
        }
        File file = new File(filepath)
        if (file.exists()) {
            println "Could not move " + filepath + " - deleting as a last resort"
            payload.delete()
        }
    }

} else if (filepath.endsWith(".tar")) {
    TarArchiveInputStream tarInputStream = new TarArchiveInputStream(new FileInputStream(payload))
    TarArchiveEntry entry;
    int offset;
    while ((entry = tarInputStream.getNextTarEntry()) != null) {
        if (!entry.isDirectory()) {
            BufferedOutputStream outputStream = null
            try {
                String filename = entry.getName()
                int i = filename.lastIndexOf('.')
                String newName = filename.substring(0, i + 1) + ts + filename.substring(i)
                String targetPath = replaceGroup(".*/([^/]+)/[^/]+\$", filepath, 1, DATA_IN_FOLDER)
                File out = new File(targetPath.substring(0, targetPath.lastIndexOf(pathSep) + 1) + newName)
                outputStream = new BufferedOutputStream(new FileOutputStream(out))
                final byte[] buffer = new byte[entry.getSize()]
                int n = 0
                offset = 0
                while ((n = tarInputStream.read(buffer, offset, buffer.length - offset)) != -1) {
                    outputStream.write(buffer, offset, n)
                }
                boolean fileSuccessfullyDeleted = payload.delete()
            } catch (Exception e) {
                println e.getMessage()
                e.printStackTrace()
                String errorPath = replaceGroup(".*/([^/]+)/[^/]+\$", filepath, 1, ERROR_FOLDER)
                boolean fileMoved = payload.renameTo(errorPath)
            } finally {
                if (outputStream != null) {
                    outputStream.flush()
                    outputStream.close()
                }
                File file = new File(filepath)
                if (file.exists()) {
                    println "Could not move " + filepath + " - deleting as a last resort"
                    payload.delete()
                }
            }
        }
    }
    tarInputStream.close()

} else if (filepath.endsWith(".zip")) {
    ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(new FileInputStream(payload))
    ZipArchiveEntry entry;
    int offset;
    while ((entry = zipInputStream.getNextZipEntry()) != null) {
        if (!entry.isDirectory()) {
            BufferedOutputStream outputStream = null
            try {
                String filename = entry.getName()
                int i = filename.lastIndexOf('.')
                String newName = filename.substring(0, i + 1) + ts + filename.substring(i)
                String targetPath = replaceGroup(".*/([^/]+)/[^/]+\$", filepath, 1, DATA_IN_FOLDER)
                File out = new File(targetPath.substring(0, targetPath.lastIndexOf(pathSep) + 1) + newName)
                outputStream = new BufferedOutputStream(new FileOutputStream(out))
                final byte[] buffer = new byte[entry.getSize()]
                int n = 0
                offset = 0
                while ((n = zipInputStream.read(buffer, offset, buffer.length - offset)) != -1) {
                    outputStream.write(buffer, offset, n)
                }
                boolean fileSuccessfullyDeleted = payload.delete()
            } catch (Exception e) {
                println e.getMessage()
                e.printStackTrace()
                String errorPath = replaceGroup(".*/([^/]+)/[^/]+\$", filepath, 1, ERROR_FOLDER)
                boolean fileMoved = payload.renameTo(errorPath)
            } finally {
                if (outputStream != null) {
                    outputStream.flush()
                    outputStream.close()
                }
                File file = new File(filepath)
                if (file.exists()) {
                    println "Could not move " + filepath + " - deleting as a last resort"
                    payload.delete()
                }
            }
        }
    }
    zipInputStream.close()
}