// Stores the data for one university.
public class University implements Comparable<University> {
    // University's rank.
    private int rank;
    // University's name.
    private String name;
    // Country or area where the university is located.
    private String location;
    // Number of students.
    private int numStudents;
    // Number of students for each staff member.
    private double studentPerStaff;
    // Percentage of students from other countries.
    private double internationalStudentPercent;
    // Female to male student ratio.
    private String femaleMaleRatio;
    // Overall ranking score.
    private double overallScore;
    // Teaching score from the source data.
    private double teachingScore;
    // Research score from the source data.
    private double researchScore;
    // Citations score from the source data.
    private double citationsScore;
    // Industry income score from the source data.
    private double industryIncomeScore;
    // International outlook score from the source data.
    private double internationalOutlookScore;

    // Creates a University object with all ranking values.
    public University(
        // Stores the university rank.
        int rank,
        // Stores the university name.
        String name, 
        // Stores the university location.
        String location, 
        // Stores the number of students.
        int numStudents, 
        // Stores the student-to-staff value.
        double studentPerStaff, 
        // Stores the international student percentage.
        double internationalStudentPercent,
        // Stores the female-to-male ratio.
        String femaleMaleRatio,
        // Stores the overall score.
        double overallScore,
        // Stores the teaching score.
        double teachingScore,
        // Stores the research score.
        double researchScore,
        // Stores the citations score.
        double citationsScore,
        // Stores the industry income score.
        double industryIncomeScore,
        // Stores the international outlook score.
        double internationalOutlookScore
    ){
        // Saves the rank in the object.
        this.rank = rank;
        // Saves the name in the object.
        this.name = name;
        // Saves the location in the object.
        this.location = location;
        // Saves the student count in the object.
        this.numStudents = numStudents;
        // Saves the student-to-staff value.
        this.studentPerStaff = studentPerStaff;
        // Saves the international student percentage.
        this.internationalStudentPercent = internationalStudentPercent;
        // Saves the female-to-male ratio.
        this.femaleMaleRatio = femaleMaleRatio;
        // Saves the overall score.
        this.overallScore = overallScore;
        // Saves the teaching score.
        this.teachingScore = teachingScore;
        // Saves the research score.
        this.researchScore = researchScore;
        // Saves the citations score.
        this.citationsScore = citationsScore;
        // Saves the industry income score.
        this.industryIncomeScore = industryIncomeScore;
        // Saves the international outlook score.
        this.internationalOutlookScore = internationalOutlookScore;
    }

    // Returns the university rank.
    public int getRank(){
        return rank;
    }

    // Returns the university name.
    public String getName(){
        return name;
    }
    
    // Returns the university location.
    public String getLocation(){
        return location;
    }

    // Returns the number of students.
    public int getNumStudents(){
        return numStudents;
    }

    // Returns the student-to-staff value.
    public double getStudentPerStaff(){
        return studentPerStaff;
    }

    // Returns the international student percentage.
    public double getInternationalStudentPercent(){
        return internationalStudentPercent;
    }

    // Returns the overall score.
    public double getOverallScore(){
        return overallScore;
    }

    // Compares two universities by rank.
    @Override
    public int compareTo(University other){
        return Integer.compare(rank, other.getRank());
    }
    // Returns a short description of the university.
    @Override
    public String toString(){
        return "Rank: " + rank + ", Name: " + name + ", Location: " + location;
    }

}