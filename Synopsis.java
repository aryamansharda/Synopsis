public class Synopsis
{
    static GenerateThumbnails generator = new GenerateThumbnails();     
    static ProcessThumbnailsHorizontal horizontal = new ProcessThumbnailsHorizontal(); 
    
    //private static final String inputFilename = "/Users/aryamansharda/Downloads//Thor The Dark World (2013) [1080p]/Thor.The.Dark.World.2013.1080p.BluRay.x264.YIFY.mp4";
    //private static final String outputFilePrefix = "/Users/aryamansharda/Documents/VideoEngine/Frames/";
    //"/Users/aryamansharda/Documents/VideoEngine/Frames/BBT-10-18.png"
    public static void main (String[] args) {

 
       //args[1] = path the the video
       //args[2] = path to the output directory
       //args[3] = name of the final image
    
       generator.run(args[0], args[1]);
       horizontal.run(args[1], args[2]); 
    }
}
