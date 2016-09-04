//@Grab(group="com.jcraft", module="jsch", version="0.1.46")
import com.jcraft.jsch.*

println()
println "Running rename_sftp_file.groovy"
println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"

java.util.Properties conf = new java.util.Properties()
conf.put "StrictHostKeyChecking", "no"

def remoteDir = payload.remoteDir
def filename = payload.filename
def path = remoteDir + filename
println "from: " + path

JSch ssh = new JSch()
Session sess = ssh.getSession "d777710", "10.47.124.151", 22
sess.with {
    setConfig conf
    setPassword "CxpDev01"
    connect()
    Channel chan = openChannel "sftp"
    chan.connect()

    ChannelSftp sftp = (ChannelSftp)chan

    try {
        sftp.rename(path, path + ".cxp")
        println "to  : " + path + ".cxp"
    } catch (all) {
        println "appears we already renamed it"
    }
    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"

    chan.disconnect()
    disconnect()
}
