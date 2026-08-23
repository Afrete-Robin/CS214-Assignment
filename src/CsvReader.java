import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CsvReader{
    public List<University> loadAsArrayList(String filePath){
        List<University> list = new ArrayList<>();
        loadInto(filePath, list);
        return list;
    }
    public List<University> loadAsLinkedList(String filePath){
        List<University> list = new LinkedList<>();
        loadInto(filePath, list);
        return list;
    }

    private void loadInto(String filePath, List<University> list){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            reader.readLine();
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                int rank = Integer.parseInt(parts[0]);
                String name = parts[1];
                String location = parts[2];
                String cleanNum = parts[3].replace("\"", "").replace(",", "");
                int numStudents = Integer.parseInt(cleanNum);
                double studentPerStaff = Double.parseDouble(parts[4]);
                double internationalStudentPercent = parsePercent(parts[5]);
                String femaleMaleRatio = parts[6];
                double overallScore = parseScore(parts[7]);
                double teachingScore = Double.parseDouble(parts[8]);
                double researchScore = Double.parseDouble(parts[9]);
                double citationsScore = Double.parseDouble(parts[10]);
                double industryIncomeScore = Double.parseDouble(parts[11]);
                double internationalOutlookScore = Double.parseDouble(parts[12]);

                University uni = new University(rank, name, location, numStudents, studentPerStaff,
                        internationalStudentPercent, femaleMaleRatio, overallScore, teachingScore,
                        researchScore, citationsScore, industryIncomeScore, internationalOutlookScore);
                list.add(uni);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Handles if data has range points which would now calculate the midpoints and store it as the new value
    private double parseScore(String rawScore){
        if(rawScore.contains("–") || rawScore.contains("-")){
            String[] bounds = rawScore.split("[–-]");
            double low = Double.parseDouble(bounds[0]);
            double high = Double.parseDouble(bounds[1]);
            double midpoint = (low + high)/2;
            return midpoint;
        }else{
            return Double.parseDouble(rawScore);
        }
    }
    //Handles if percentage is empty and converts % values to double which can be used later
    private double parsePercent(String rawPercent){
        String cleanPercent = rawPercent.replace("%", "");
        if(cleanPercent.isEmpty()){
            return 0;
        }else{
            return Double.parseDouble(cleanPercent);
        }
    }
}
