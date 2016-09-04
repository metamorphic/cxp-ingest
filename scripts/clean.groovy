if (payload.exists()) {
    payload.delete()
    println "deleted " + payload.getAbsolutePath()
}