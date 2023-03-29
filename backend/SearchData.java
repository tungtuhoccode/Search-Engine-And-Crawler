package ProjectFiles;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchData {
    private DirectoryHelper dirHelp;

    public SearchData(){
        System.out.println("new directoryHelper will be created in SearchData");
        dirHelp = new DirectoryHelper();
    }

    //This is to avoid creating a new Directory helper when search
    public SearchData(boolean isSearchClassCall,DirectoryHelper directoryHelper){
        this.dirHelp = directoryHelper;
    }

    public Double getPageRank(String url){
        try{
           BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getPageRankFile(url)));
           Double pageRank = Double.parseDouble(reader.readLine().strip());
           reader.close();
           return pageRank;
        }
        catch (FileNotFoundException e){
            System.out.println("No page with url exists.");
            return -1.0;
        }
        catch (IOException e){
            System.out.println("IO exception");
        }
        return -1.0;
    }

    public String getTitle(String url){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getTitleFile(url)));

            String title = reader.readLine().strip();
            return title;
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            return null;
        }
        catch (IOException e){
            System.out.println("IO exception");
        }
        return null;
    }

    public double getTfIdf(String url,String word){

      try{
          BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getTfIdfWordFile(url,word)));
          Double tfidf  = Double.parseDouble(reader.readLine().strip());
          return tfidf;
      }
      catch (FileNotFoundException e){
          return 0.0;
      }
      catch (IOException e){
          System.out.println("IO exception");
      }
      return -1;
    }

    public double getIdf(String word){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getIdfFile(word)));
            String line = reader.readLine();
            Double idf = Double.parseDouble(line);
            return idf;
        }
        catch(FileNotFoundException e){
            return 0.0;
        }
        catch(IOException e){
            System.out.println("IO except getTF");
        }
        return -1;
    }

    public double getTF(String url, String word){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getTfFileForWord(url,word)));
            String line = reader.readLine();
            Double tf = Double.parseDouble(line);
            return tf;
        }
        catch(FileNotFoundException e){
            return 0.0;
        }
        catch(IOException e){
            System.out.println("IO except getTF");
        }
        return 0;
    }

    public List<String> getOutgoingLinks(String url){
        try{
            ArrayList<String> outgoing = new ArrayList<String>();

            BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getOutUrlFilePath(url)));
            while(true){
                String line = reader.readLine();
                if(line == null){
                    break;
                }
                outgoing.add(line.strip());
            }

            return outgoing;
        }
        catch (FileNotFoundException e){
            System.out.println("There is no such file");
        }
        catch (IOException i){
            System.out.println("IO exception get outgoing link");
        }
        return null;
    }

    public List<String> getIncomingLinks(java.lang.String url){
        try{
            ArrayList<String> incoming = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(dirHelp.getInUrlFilePath(url)));
            while(true){
                String line = reader.readLine();
                if(line==null){
                    break;
                }
                incoming.add(line.strip());
            }

            return incoming;
        }
        catch (FileNotFoundException e){
            System.out.println("There is no such file");
        }
        catch (IOException i){
            System.out.println("IO exception get outgoing link");
        }
        return null;
    }



}
