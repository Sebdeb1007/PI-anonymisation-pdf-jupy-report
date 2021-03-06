
package anon;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


import java.util.concurrent.Callable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;





@Command(name = "anonymization", mixinStandardHelpOptions = true, version = "anonymization 1.0",
         description = "anonymization of a file pdf or jupyter notebook")
class Main implements Callable<Integer> {

        @Parameters(index = "0", description = "The file or directory to anonymize")
    private String file;

    @Option(names = {"-o", "--output"}, description = "The output repertory (default: current directory)")
    private String output = "";

    @Option(names = {"-h","--hide"}  , description = "Hide the file")
    private boolean hide;

    @Option(names = {"-r","--reveal"}  , description = "Reveal the file ")
    private boolean reveal;

    

    
  
    @Override
    public Integer call() throws Exception { 
         try {
            Path path = Paths.get(file);
            if (Files.isDirectory(path)) {
                File directoryPath = new File(file);
                String contents[] = directoryPath.list();
                if (hide){
                    
                     for(int i=0; i<contents.length; i++) {
                        anon(file + contents[i],  output);
                    }
                } 
                else if(reveal){
                    for(int i=0; i<contents.length; i++) {
                        reveal(file + contents[i],  output);
                    }
                }
            }
            else {
                if (hide){
                    anon(file,  output);
                } 
                else if(reveal){
                   reveal(file,  output);
                }   
            }
            
        }catch (Exception e) {
             System.out.println(e.getMessage());
             System.out.println("File not found");
        }


         // list all the file on a directory
        return 0;
        //
    }
    /**
     * This function takes in a file and an output file, and if the file is a pdf or jupyter notebook,
     * it will anonymize the file and save it to the output file
     * 
     * @param file the file you want to anonymize
     * @param output The output directory for the anonymized files
     */
    private void anon(String file, String output) {
                String fe = "";
                int i = file.lastIndexOf('.');
                if (i > 0) {
                    fe = file.substring(i+1);
                }
		        if(fe.equals("pdf")){
                    AnonPDF.anonPDF(file, output);
                }
                else if(fe.equals("ipynb")){
                    AnonJPY.anonJPY(file, output);
                }
                else{
                    System.out.println(file + " is not a pdf or jupyter file");
                }
    }
   /**
    * If the file extension is pdf, then call the revealPDF function from the AnonPDF class. If the
    * file extension is ipynb, then call the revealJPY function from the AnonJPY class. Otherwise,
    * print an error message
    * 
    * @param file the file you want to reveal
    * @param output The output file name
    */
    private void reveal(String file, String output) {
        String fe = "";
        int i = file.lastIndexOf('.');
        if (i > 0) {
            fe = file.substring(i+1);
        }
        if(fe.equals("pdf")){
            AnonPDF.revealPDF(file, output);
        }
        else if(fe.equals("ipynb")){
            AnonJPY.revealJPY(file, output);
        }
        else{
            System.out.println(file + " is not a pdf or jupyter file");
        }
    }   
       

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}