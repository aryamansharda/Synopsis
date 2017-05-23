public class Synopsis
{
    static GenerateThumbnails generator = new GenerateThumbnails();     
    static ProcessThumbnailsHorizontal horizontal = new ProcessThumbnailsHorizontal(); 

    public static void main (String[] args) {   
       generator.run(args[0], args[1]);
       horizontal.run(args[1], args[2]); 
    }
}
