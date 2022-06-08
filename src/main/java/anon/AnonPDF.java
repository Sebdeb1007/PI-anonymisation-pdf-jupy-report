

package anon;


import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.apache.pdfbox.contentstream.operator.*;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdfparser.*;
import org.apache.pdfbox.cos.*;



public class AnonPDF {
    
    /**
     * It takes a PDF file and an output path as parameters, then it parses the PDF file and removes
     * the rows that contain the information that we want to hide
     * 
     * @param file the path to the PDF file you want to anonymize
     * @param output the output directory
     */
    public static void anonPDF(String file, String output) {
        
        String[] infos = getInfos(file);
        PDDocument doc = null;
    
        int path_name = (int) Math.floor(Math.random()*(1000000000-0+1)+0) ;
        String path_name_str = Integer.toString(path_name) + ".pdf";

        try {

              //load PDDocument
              File pdf = new File(file);
              doc = PDDocument.load(pdf);
              
              //For each PDPage in Doc
              for (PDPage page : doc.getPages()) {
                //parser used to get  All the token in a page 
                PDFStreamParser parser = new PDFStreamParser(page);
                parser.parse();
                List<Object> tokens = parser.getTokens();
                List<List> newTokens = new ArrayList<List>();
                List<Object> subRow = new ArrayList<Object>();
                COSFloat y = new COSFloat(0);
                float previousFloat = 0;
                for( int j=0; j<tokens.size(); j++ ){
                  Object next = tokens.get( j );
                  if (next instanceof Operator) {
                    Operator op = (Operator) next;
                    if (op.getName().equals("Tm")) {
                      y = (COSFloat)tokens.get( j-1 );
                    }
                  }
                  if(previousFloat == y.floatValue()){
                    subRow.add(next);
                  }else{
                    previousFloat = y.floatValue();
                    newTokens.add(subRow);
                    subRow = new ArrayList<Object>();
                    subRow.add(next);
                  }
                }
               
               
               
                int table = 0;
                int count = 0;
                Set<Integer> toBeRemove = new HashSet<Integer>();

                while(table < newTokens.size()){
                    int row = 0;
                    int len = newTokens.get(table).size();
                    String rowString = "";
                    while(row < len){
                        Object next = tokens.get( count );
                        String s = "";
                        if (next instanceof Operator) {
                            Operator op = (Operator) next;
                            if (op.getName().equals("TJ")) {
                                COSArray previous = (COSArray)tokens.get( count-1 );
                                
                                for( int k=0; k<previous.size(); k++ )
                                {   
                                    Object arrElement = previous.getObject( k );
                                    
                                    if( arrElement instanceof COSString )
                                    {    
                                        COSString cosString = (COSString)arrElement;
                                        String string = cosString.getString();
                                        s += string;
                                    }
                                } 
                                rowString += s;
                            }   
                        }
                        count++;
                        row++;
                    }
                    
                    Pattern pattern1 = Pattern.compile(infos[0], Pattern.CASE_INSENSITIVE);
                    Pattern pattern2 = Pattern.compile(infos[1], Pattern.CASE_INSENSITIVE);
                    Pattern pattern3 = Pattern.compile(infos[2], Pattern.CASE_INSENSITIVE);

                    // Matching the pattern with the rowString.
                    Matcher matcher1 = pattern1.matcher(rowString);
                    if (matcher1.find()) {
                        toBeRemove.add(table);
                    }
                    Matcher matcher2 = pattern2.matcher(rowString);
                    if (matcher2.find()) {
                        toBeRemove.add(table);
                    }
                    Matcher matcher3 = pattern3.matcher(rowString);
                    if (matcher3.find()) {
                        toBeRemove.add(table);
                    }
                    table++;
                }
                
                table = 0;
                count = 0;
                for (List list : newTokens) {
                    for (Object ob : list) {
                        Object next = tokens.get(count);
                        if (toBeRemove.contains(table) ){
                            if (next instanceof Operator) {
                                Operator op = (Operator) next;
                                if(op.getName().equals( "TJ" )){
                                    Operator newOp = Operator.getOperator("token");
                                    tokens.set(count,(Object) newOp);
                                }
                            }
                        }
                        count++;
                        PDStream updatedStream = new PDStream(doc);
                        OutputStream out = updatedStream.createOutputStream();
                        ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
                        tokenWriter.writeTokens( tokens );
                        page.setContents(updatedStream);
                        
                        out.close();
                   } 
                   table++;
                }
                doc.save(output+path_name_str); 
              }
              doc.close();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * It takes a PDF file, loads it into a PDDocument, parses the tokens, and replaces the token
     * operator with the TJ operator
     * 
     * @param file the path to the PDF file you want to reveal
     * @param output the output directory
     */
    public static void revealPDF(String file, String output) {
        PDDocument doc = null;
        String name = "";
        String id = "";
        try {
            File pdf = new File(file);
            id = pdf.getName();
            name = pdf.getName();
            doc = PDDocument.load(pdf);
            for (PDPage page : doc.getPages()) {
                PDFStreamParser parser = new PDFStreamParser(page);
                parser.parse();
                List<Object> tokens = parser.getTokens();
                
                for( int j=0; j<tokens.size(); j++ ){ 
                    Object next = tokens.get( j );
                    if (next instanceof Operator) {
                        Operator op = (Operator) next;
                        if (op.getName().equals("token")){
                            Operator newOp = Operator.getOperator("TJ");
                            tokens.set(j,(Object) newOp);
                        }
                    }
                    PDStream updatedStream = new PDStream(doc);
                    OutputStream out = updatedStream.createOutputStream();
                    ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
                    tokenWriter.writeTokens( tokens );
                    page.setContents(updatedStream);
                    out.close();
                }
            doc.save(output+ id.substring(0,id.length()-4)+"_" + name);  
            }
            doc.close();
            pdf.delete();
    
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String[] infos  = getInfos(output+id.substring(0,id.length()-4)+"_" + name);
        Path fileToBeRename = Paths.get(output+id.substring(0,id.length()-4)+"_" + name);
        Path rename = Paths.get(output+id.substring(0,id.length()-4)+"_" + infos[0]+".pdf");
        try {
            Files.move(fileToBeRename, rename);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   /**
    * It takes a PDF file as an input and returns an array of strings containing the name, mail and
    * number of the user
    * 
    * @param file The path to the PDF file.
    * @return The method returns an array of strings.
    */
    private static String[] getInfos(String file) {
        File pdf = new File(file);
        String[] infos = new String[3];
        String text = "";
        // A regular expression that is used to find the name, mail and number of the user.
        Pattern pattern1 = Pattern.compile("name:", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("mail:", Pattern.CASE_INSENSITIVE);
        Pattern pattern3 = Pattern.compile("num:", Pattern.CASE_INSENSITIVE);
        try {
            PDDocument doc = PDDocument.load(pdf);
            PDFTextStripper reader = new PDFTextStripper();
            reader.setStartPage(1);
            reader.setEndPage(1);
            text = reader.getText(doc);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lines[] = text.split("\\r?\\n");
        for (String string : lines) {
           
            // Matching the pattern with the rowString.
            Matcher matcher1 = pattern1.matcher(string);
            if (matcher1.find()) {
                infos[0] = string.split(":")[1];
            }
            Matcher matcher2 = pattern2.matcher(string);
            if (matcher2.find()) {
                infos[1] =  string.split(":")[1];
            }
            Matcher matcher3 = pattern3.matcher(string);
            if (matcher3.find()) {
                infos[2] =  string.split(":")[1];
            }

        }
        
        return infos;
    }
}
