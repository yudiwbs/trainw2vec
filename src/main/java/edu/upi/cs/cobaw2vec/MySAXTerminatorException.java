package edu.upi.cs.cobaw2vec;

import org.xml.sax.SAXException;

/**
 * Created by yudiwbs on 03/11/2016.
 * Exception agar saxparser bisa berhenti ditengah jalan
 * dengan ukuran file >1GB, perlu ada mekanisme untuk stop
 */

public class MySAXTerminatorException extends SAXException {

}
