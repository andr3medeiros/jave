package it.sauronsoftware.jave;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
    	super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    
    protected File getTestFile(String path) {
    	ClassLoader classLoader = this.getClass().getClassLoader();
    	
        return new File(classLoader.getResource(path).getFile());
    }

    public void testVideoToAudio() throws IOException, IllegalArgumentException, InputFormatException, EncoderException {
    	File testFile = getTestFile("surfando_de_helicoptero.mp4");
    	File outFile = File.createTempFile("jave", ".ogg");
    	
    	Encoder encoder = new Encoder();
    	
    	EncodingAttributes attributes = new EncodingAttributes();
		attributes.setFormat("ogg");
		AudioAttributes audioAttributes = new AudioAttributes();
		audioAttributes.setSamplingRate(44800);
		audioAttributes.setChannels(1);
		attributes.setAudioAttributes(audioAttributes);
		encoder.encode(testFile, outFile, attributes);
		
		MultimediaInfo sourceInfo = encoder.getInfo(testFile);
		MultimediaInfo targetInfo = encoder.getInfo(outFile);
		
        assertEquals( "Right channel count",  targetInfo.getAudio().getChannels(), 1);
        assertEquals( "Right sampling rate",  targetInfo.getAudio().getSamplingRate(), 44800);
        assertEquals( "Right format",  targetInfo.getFormat(), "ogg");
        assertTrue("Duration is within right range 'cause it's never gone equal", sourceInfo.getDuration() >= targetInfo.getDuration());
    }
}
