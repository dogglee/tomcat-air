package com.douglee.tomcatair.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
	private static final int BUFFER_SIZE=1024; 
	private IOUtils(){
		
	}
	public static byte[] readBytes(InputStream is) throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(true) {
            int length = is.read(buffer);
            if(-1==length)
                break;
            baos.write(buffer, 0, length);
            if(length!=BUFFER_SIZE) // 下次就没有了
                break;
        }
        byte[] result =baos.toByteArray();
        return result;
    }
}
