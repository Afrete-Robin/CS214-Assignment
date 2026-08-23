public class University implements Comparable<University> {
    private int rank;
    private String name;
    private String location;
    private int numStudents;
    private double studentPerStaff;
    private double internationalStudentPercent;
    private String femaleMaleRatio;
    private double overallScore;
    private double teachingScore;
    private double researchScore;
    private double citationsScore;
    private double industryIncomeScore;
    private double internationalOutlookScore;
    
    public University(
        int rank,
        String name, 
        String location, 
        int numStudents, 
        double studentPerStaff, 
        double internationalStudentPercent,
        String femaleMaleRatio,
        double overallScore,
        double teachingScore,
        double researchScore,
        double citationsScore,
        double industryIncomeScore,
        double internationalOutlookScore
    ){
        this.rank = rank;
        this.name = name;
        this.location = location;
        this.numStudents = numStudents;
        this.studentPerStaff = studentPerStaff;
        this.internationalStudentPercent = internationalStudentPercent;
        this.femaleMaleRatio = femaleMaleRatio;
        this.overallScore = overallScore;
        this.teachingScore = teachingScore;
        this.researchScore = researchScore;
        this.citationsScore = citationsScore;
        this.industryIncomeScore = industryIncomeScore;
        this.internationalOutlookScore = internationalOutlookScore;
    }

    public int getRank(){
        return rank;
    }

    public String getName(){
        return name;
    }
    
    public String getLocation(){
        return location;
    }

    public int getNumStudents(){
        return numStudents;
    }

    public double getStudentPerStaff(){
        return studentPerStaff;
    }

    public double getInternationalStudentPercent(){
        return internationalStudentPercent;
    }

    public String getFemaleMaleRatio(){
        return femaleMaleRatio;
    }

    public double getOverallScore(){
        return overallScore;
    }
    public double getTeachingScore(){
        return teachingScore;
    }
    public double getResearchScore(){
        return researchScore;
    }
    public double getCitationsScore(){
        return citationsScore;
    }
    public double getIndustryIncomeScore(){
        return industryIncomeScore;
    }
    public double getInternationalOutlookScore(){
        return internationalOutlookScore;
    }


    @Override
    public int compareTo(University other){
        return Integer.compare(rank, other.getRank());
    }
    @Override
    public String toString(){
        return "Rank: " + rank + ", Name: " + name + ", Location: " + location;
    }

}