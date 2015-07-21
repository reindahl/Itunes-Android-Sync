package test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import org.junit.Test;


public class hashTest {

	@SuppressWarnings("unused")
	@Test
	public void Test(){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			InputStream is = Files.newInputStream(Paths.get("Settings2"));
			DigestInputStream dis = new DigestInputStream(is, md);
			byte[] digest = md.digest();
			System.out.println(Arrays.toString(digest));
		} catch (Exception e) {
			e.printStackTrace();
		}
		fail();
		

	}
}
