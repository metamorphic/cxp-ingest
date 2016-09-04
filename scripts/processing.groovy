import static org.springframework.xd.tuple.TupleBuilder.tuple;

String sourcePath = payload.getAbsolutePath()
def targetPath
if (sourcePath.endsWith(".processing")) {
    targetPath = sourcePath
} else {
    targetPath = sourcePath + ".processing"
    File sourceFile = new File(sourcePath)
    File targetFile = new File(targetPath)
    boolean fileMoved = sourceFile.renameTo(targetFile)
}

return tuple().of("absoluteFilePath", targetPath)