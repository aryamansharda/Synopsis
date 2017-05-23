import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IContainer;

public class GenerateThumbnails {
    private static String inputFilename;
    private static String outputFilePrefix;
    
    public static final double SECONDS_BETWEEN_FRAMES = 1;
   
    private static int mVideoStreamIndex = -1;
    
    private static double videoLength = 1; 
    private static long mLastPtsWrite = Global.NO_PTS;
    private static long framesProcessed = 1; 
    
    public static final long MICRO_SECONDS_BETWEEN_FRAMES = (long)(Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);
 
    public void run(String inputFilename, String outputDirectory) {
        this.inputFilename = inputFilename; 
        this.outputFilePrefix = outputDirectory; 
        
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);        
        mediaReader.addListener(new ImageSnapListener());
        
        IContainer container = IContainer.make();
        container.open(inputFilename, IContainer.Type.READ, null);
        videoLength = container.getDuration()/1000000;
        
        while (mediaReader.readPacket() == null);
    }
    
    private static class ImageSnapListener extends MediaListenerAdapter {
        public void onVideoPicture(IVideoPictureEvent event) {          
            if (event.getStreamIndex() != mVideoStreamIndex) {
                if (mVideoStreamIndex == -1) {
                    mVideoStreamIndex = event.getStreamIndex();
                } else {
                    return;
                }
            }

            if (mLastPtsWrite == Global.NO_PTS) {
                mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
            }

            if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {                                
                String outputFilename = dumpImageToFile(event.getImage());
                
                int totalSecs = (int)((event.getTimeStamp()) / Global.DEFAULT_PTS_PER_SECOND);
                
                int hours = totalSecs / 3600;
                int minutes = (totalSecs % 3600) / 60;
                int seconds = totalSecs % 60;

                System.out.printf(String.format("%02d:%02d:%02d", hours, minutes, seconds) + " of video processed. (%.1f%%) \n", (double)(totalSecs/videoLength) * 100); 
                
                mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
            }
        }
        
        private String dumpImageToFile(BufferedImage image) {
            try {
                String outputFilename = outputFilePrefix + framesProcessed++ + ".png";
                ImageIO.write(image, "png", new File(outputFilename));
                return outputFilename;
            } 
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }   
}