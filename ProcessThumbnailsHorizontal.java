import java.awt.image.BufferedImage;
import java.awt.Color; 
import java.util.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ProcessThumbnailsHorizontal
{
    static ArrayList<Color> colors = new ArrayList<Color>(); 
    
    static int width; 
    static int height; 
    
    static final String[] EXTENSIONS = new String[]{ "png" };
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    
    public void run(String outputFilePrefix, String outputImageName) {       
        File dir = new File(outputFilePrefix);        
        if (dir.isDirectory()) { 
            File[] files = dir.listFiles(IMAGE_FILTER);            
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    int n1 = extractNumber(o1.getName());
                    int n2 = extractNumber(o2.getName());
                    return n1 - n2;
                }
    
                private int extractNumber(String name) {
                    int i = 0;
                    try {
                        int s = 0;
                        int e = name.lastIndexOf('.');
                        String number = name.substring(s, e);
                        i = Integer.parseInt(number);
                    } catch(Exception e) {
                        i = 0;
                    }
                    return i;
                }
            });
            
            width = files.length;
            height = 400; 
                    
            for (final File f : files ) {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(f);                    
                    System.out.println(f.getName());                                     
                    colors.add(averageColor(img, 0, 0, img.getWidth(), img.getHeight())); 
                } catch (final IOException e) { }
            }            
        }
        
        BufferedImage finalImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                try {
                    finalImage.setRGB(x, y, colors.get(x).getRGB());
                } catch (Exception e) {}
            }
        }       
        
        File outputfile = new File(outputImageName); 
        try {
            ImageIO.write(finalImage, "png", outputfile);
        } catch (Exception e) {
            System.out.println("Exception occured"); 
        }
    }
    
    public static Color averageColor(BufferedImage bi, int x0, int y0, int w, int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen(); 
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        return new Color((int)(sumr / num), (int)(sumg / num), (int)(sumb / num));
    }
}
