import org.codehaus.groovy.runtime.DateGroovyMethods

sleep(30000) // let it finish copying

def filepath = payload.filepath
def i = filepath.lastIndexOf('/')
def processingDir = filepath.substring(0, i + 1)

String sourceFilename = processingDir + "events.csv"
File source = new File(sourceFilename)
String targetFilename = processingDir + "events.tmp"
File target = new File(targetFilename)
boolean fileMoved = source.renameTo(target)

//source.createNewFile();

//def exportPass = "PGPASSWORD=" + payload.password
//def proc1 = ["export", exportPass].execute()
//proc1.waitForOrKill(500)

sleep(5000) // ensure file is renamed

int exitValue = -1;
def sout = new StringBuffer(), serr = new StringBuffer()
if (fileMoved && target.exists()) {

    // specifying command line arguments is not too bad once you learn
    // to use Groovy to worry about escaping for you
    // for this magic to happen, you must specify each command line
    // argument, which is separated by a space, as an element in an
    // array - NOT within a string!!

    def command = ["/usr/bin/psql", "-h", payload.host, "-d", "cxpdev", "-U", payload.user, "-c", "\\COPY ${payload.table} FROM '${target.getAbsolutePath()}' QUOTE '\"' CSV"]
    println command.join(' ')
    def proc = command.execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(600000) // kill after 10 min
    println "out>"
    println "$sout"
    println "err>"
    println "$serr"
    proc.text.eachLine { println it }
    exitValue = proc.exitValue()
    println "exit value: " + exitValue
} else {
    println "could not find " + target.getAbsolutePath()
}
if (exitValue == 0) {
    def ts = DateGroovyMethods.format(new Date(), "yyyyMMddHHmmss")
    String archiveFilename = payload.archiveDir + "events.csv." + ts
    File archive = new File(archiveFilename)
    boolean fileMoved2 = target.renameTo(archive)
}