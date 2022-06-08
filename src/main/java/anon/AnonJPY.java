package anon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AnonJPY
 */
public class AnonJPY {

   /**
    * It takes a file as input, and returns a file with the same content, but with the name, email, and
    * phone number fields anonymized
    * 
    * @param file the path to the file you want to anonymize
    * @param output the path to the output directory
    */
    public static void anonJPY(String file, String output){
        String[] infos = getInfos(file);
        File f = new File(file);
        BufferedReader br;
        FileWriter writer = null;

        int path_name = (int) Math.floor(Math.random()*(1000000000-0+1)+0) ;
        String path_name_str = Integer.toString(path_name) + ".ipynb";
        try {
            br = new BufferedReader(new FileReader(f));
            String st;
            Pattern pattern1 = Pattern.compile(infos[0].substring(0,infos[0].length() - 4), Pattern.CASE_INSENSITIVE);
            Pattern pattern2 = Pattern.compile(infos[1].substring(0,infos[1].length() - 4), Pattern.CASE_INSENSITIVE);
            Pattern pattern3 = Pattern.compile(infos[2], Pattern.CASE_INSENSITIVE);
            String name ="";
            String mail ="";
            String num ="";
            int count = 0 ;
            while ((st = br.readLine()) != null){
                count++;
            }
            br.close();
            br = new BufferedReader(new FileReader(f));
            String  newText2  = "";
            int count2 = 0;
            while ((st = br.readLine()) != null){
                Matcher matcher1 = pattern1.matcher(st);
                Matcher matcher2 = pattern2.matcher(st);
                Matcher matcher3 = pattern3.matcher(st);
                if (matcher1.find() && name.equals("")) {
                    name = st ;
                    newText2 = newText2 + "   \"name\"," + System.lineSeparator();
                }
                else if (matcher2.find()  && mail.equals("")) {
                    mail = st ;
                    newText2 = newText2 + "   \"mail\"," + System.lineSeparator();
                }
                
                else if (matcher3.find() && num.equals("")) {
                    num = st;
                    newText2 = newText2 + "   \"num\"" + System.lineSeparator();
                }
                else{
                    newText2 = newText2 + st + System.lineSeparator();
                }
                
                if (count2 == count-3){
                    newText2 = newText2 + name.substring(0,name.length()-1)  +":" +"\"v\","  + System.lineSeparator()  ;
                    newText2 = newText2 + mail.substring(0,mail.length()-1) +":" +"\"v\"," + System.lineSeparator();
                    newText2 = newText2 + num  +":" +"\"v \"," + System.lineSeparator();
                }
                count2++;
            }
            br.close();
            writer = new FileWriter(output+path_name_str);
            writer.write(newText2);
            writer.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    // Declaring a string variable
      
    }
    /**
     * This function takes a file and an output directory as input, reads the file, removes the last
     * three lines, and writes the file to the output directory with the name
     * "reveal_"+name.substring(10,name.length()-8)+".ipynb"
     * 
     * @param file the file to be decrypted
     * @param output the directory where the output file will be saved
     */
    public static void revealJPY(String file,String output){
        File f = new File(file);
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String string;
            int count = 0;
            List<String> lines = new ArrayList<String>();
            while ((string = br.readLine()) != null){
                lines.add(string);
            } 
            br.close(); 
            String name = lines.get(lines.size()-5);
            String mail = lines.get(lines.size()-4);
            String num = lines.get(lines.size()-3);
            lines.remove(lines.size()-5);
            lines.remove(lines.size()-4);
            lines.remove(lines.size()-3);
            String tot = "";
            for (String string2 : lines) {
                if (count == 6){
                    tot = tot + name.substring(0,name.length()-5)+","+System.lineSeparator();
                }
                else if(count == 7){
                    tot = tot + mail.substring(0,mail.length()-5)+"," + System.lineSeparator();
                }
                else if (count == 8){
                    tot = tot + num.substring(0,num.length()-6) + System.lineSeparator();
                }
                else{
                    tot = tot + string2 + System.lineSeparator();
                }
                count++;
            }
            File myObj = new File(output+ f.getName().substring(0,f.getName().length()-6)+"_"+name.substring(10,name.length()-8)+".ipynb");
            FileWriter writer = new FileWriter(output+f.getName().substring(0,f.getName().length()-6)+"_"+name.substring(10,name.length()-8)+".ipynb");
            writer.write(tot);
            writer.close();
            f.delete();

           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * It reads a file and returns an array of strings containing the values of the lines that start
     * with "name:", "mail:" and "num:"
     * 
     * @param file the file path
     * @return An array of strings.
     */
    public static String[]  getInfos(String file){
        String[] infos = new String[3];
        infos[0] = new String();
        infos[1] = new String();
        infos[2] = new String();
        Pattern pattern1 = Pattern.compile("name:", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("mail:", Pattern.CASE_INSENSITIVE);
        Pattern pattern3 = Pattern.compile("num:", Pattern.CASE_INSENSITIVE);
        File f = new File(file);
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String string;
            while ((string = br.readLine()) != null){
                Matcher matcher1 = pattern1.matcher(string);
                Matcher matcher2 = pattern2.matcher(string);
                Matcher matcher3 = pattern3.matcher(string);
                if (matcher1.find()) {
                    infos[0] = string.split(":")[1];
                }
                if (matcher2.find()) {
                    infos[1] =  string.split(":")[1];
                }
                if (matcher3.find()) {
                    infos[2] =  string.split(":")[1];
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        return infos;
    }
}
