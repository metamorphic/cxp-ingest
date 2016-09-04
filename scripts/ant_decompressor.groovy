def filepath = payload.getAbsolutePath()
if (filepath.endsWith(".gz")) {
    def ant = new AntBuilder()
    //ant.gunzip(src: filepath, dest: "/Users/markmo/data/cxp/in"){ mapper(type: "flatten")}
    //ant.gunzip(src: filepath, dest: "/Users/markmo/data/cxp/in/.")
    ant.gunzip(src: filepath)
    boolean fileSuccessfullyDeleted = payload.delete()
} else if (filepath.endsWith(".tar")) {
    def ant = new AntBuilder()
    //ant.untar(src: filepath, dest: "/Users/markmo/data/cxp/in/.")
    ant.untar(src: filepath, dest: filepath.substring(0, filepath.lastIndexOf("/")))
    boolean fileSuccessfullyDeleted = payload.delete()
} else if (filepath.endsWith(".zip")) {
    def ant = new AntBuilder()
    //ant.unzip(src: filepath, dest: "/Users/markmo/data/cxp/in/.")
    ant.unzip(src: filepath)
    boolean fileSuccessfullyDeleted = payload.delete()
}