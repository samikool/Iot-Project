/**
 * Date is class that represents a date by keeping track of its year, month, and day.
 * There are several constructors available for the Date class, and if any of the instance
 * variables are not explicitly passed a value they will default to 1. Date also has a built in
 * validation check so the date can never be set to a invalid value.
 * @author Sam Morgan
 */

public class Date {
    /**
     * year is an integer that represents what the year is on the date instantiated. It can be set with the {@link #setYear(int)} method and
     * its value can be retrieved with the {@link #getYear()} method.
     */
    private int year;
    /**
     * month is an integer that represents what the month is on the date instantiated. It can be set with the {@link #setMonth(int)} method and
     * its value can be retrieved with the {@link #getMonth()} method. For getting the String equivalent of the month use
     * one of the {@link #monthToString()} methods
     *
     */
    private int month;
    /**
     * day is an integer that represents what the day is on the date instantiated. It can be set with the {@link #setDay(int)} and its value can be
     * retrieved with the {@link #getDay()} method.
     */
    private int day;
    /**
     * monthStrings[] is an array of Strings that holds the names of the 12 months as Strings. These Strings are accessed by either
     * {@link #monthToString()} on a current {@link Date} object or statically by {@link #monthToString(int)} to provide
     * the user with a month as a String instead of an integer.
     */
    private static String[] monthStrings = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    /**
     * No argument constructor will initialize the date to January 1st, 1.
     */
    public Date(){
        this(1,1,1); //Delegate to the most specific constructor
    }
    /**
     * One argument constructor will set the date to January 1st, yearGiven if it is a valid date.
     * @param year the integer to set as the year. Must be 0 or greater.
     * @throws IllegalArgumentException if year provided is invalid see {@link #isValid()} for more detail.
     */
    public Date(int year){
        this(year,1,1); //Delegate to the most specific constructor
    }
    /**
     * Two argument constructor will set the date to monthGiven 1st, yearGiven if it is a valid date.
     * @param year the integer to set the month. Must be 0 or greater.
     * @param month the integer to set as the month. Must be 1-12.
     * @throws IllegalArgumentException if year provided is invalid see {@link #isValid()} for more detail.
     */
    public Date(int year, int month){
        this(year,month,1); //Delegate to the most specific constructor
    }
    /**
     * One argument constructor that takes an already instantiated {@link Date}, and instantiates a {@link Date} with the same date.
     * @param date the date to be copied into the new Date object.
     * @throws IllegalArgumentException if year provided is invalid see {@link #isValid()} for more detail
     */
    public Date(Date date){
        this(date.getYear(), date.getMonth(), date.getDay());   //Delegate to the most specific constructor
    }

    /**
     * 3 argument constructor that will set the date to monthGiven dayGiven, yearGiven if it is a valid date.
     * Uses {@link #isValid()} to verify the date is valid.
     * @param year the integer to set the year. Must be 0 or greater.
     * @param month the integer to set the month. Must be 1-12.
     * @param day the integer to set the day as. Must be a valid day on the calender. ie. if month is February day = 30 is invalid
     * @throws IllegalArgumentException if the date provided to the constructor is invalid in some way. See {@link #isValid()} for more detail.
     */
    //This constructor does all the instantiating for the other constructors
    //after assigning values it calls the isValid method to make sure the date is valid.
    public Date(int year, int month, int day){
       this.year = year;
       this.month = month;
       this.day = day;
       isValid();
    }

    /**
     * Get method that returns the integer value of the {@link Date}'s year.
     * @return value of {@link #year} as an integer.
     */
    public int getYear(){return year;}
    /**
     * Get method that returns the integer value of the {@link Date}'s month.
     * @return the value of {@link #month} as an integer
     */
    public int getMonth(){return month;}
    /**
     * Get method that returns the integer value of the {@link Date}'s day.
     * @return the value of {@link #day} as an integer
     */
    public int getDay(){return day;}

    /**
     * Set method that will set the {@link Date}'s year. This method will also validate the date after it is called to make
     * sure the change is not invalid.
     * @param year integer to set as the year
     * @throws IllegalArgumentException if year provided is invalid see {@link #isValid()} for more detail.
     */
    public void setYear(int year){
        this.year = year;
        isValid();
    }

    /**
     * Set method that will set the {@link Date}'s month. This method will also validate the date after it is called to make
     * sure the change is not invalid.
     * @param month integer to set as the month
     * @throws IllegalArgumentException if month provided is invalid see {@link #isValid()} for more detail.
     */
    public void setMonth(int month){
        this.month = month;
        isValid();
    }

    /**
     * Set method that will set the {@link Date}'s day. When this method is called, it will also validate the date
     * after setting the day to make sure the new date is valid.
     * @param day integer to set as the day
     * @throws IllegalArgumentException if day provided is invalid see {@link #isValid()} for more detail.
     */
    public void setDay(int day){
        this.day = day;
        isValid();
    }

    /**
     * isValid is a method that will check the date and make sure it is valid after either day, month, or year are set
     * @throws IllegalArgumentException if year of the date is less than 0.
     * @throws IllegalArgumentException If month is an invalid value ie. 0 or 13 or if the provided
     * month does not contain the current day that the dat has. Ie. if previous date is January 31st and
     * you attempt to set the month to February without changing the day it will throw an IllegalArgumentException
     * @throws IllegalArgumentException If day is an invalid value based on the already assigned month and year
     * ie. January 32nd, April 31st, or February 29th if the year is not a leap year.
     */
    private void isValid(){
        if(year < 0){
            throw new IllegalArgumentException("Year must be 0 or greater");
        }

        if(month < 1 || month > 12){
            throw new IllegalArgumentException("Month must be a number 1-12");
        }

        if(this.month == 4 || this.month == 6 || this.month == 9 || this.month == 11){
            if(day > 30 || day < 1){
                throw new IllegalArgumentException("For the month "+ monthToString(this.month) + " the day must be 1-30");
            }
        }
        else if(this.month == 2){
            if(day > 29 || day < 1){
                throw new IllegalArgumentException("For the month " + monthToString(this.month) + " the day must be 1-28");
            }
            else if(day == 29){
                if( (this.year % 4 != 0 || this.year % 100 != 0) && this.year % 400 != 0){
                    throw new IllegalArgumentException("This year is not a leap year so for the month February the day must be 1-28");
                }
            }
        }
        else{
            if(day > 31 || day < 1){
                throw new IllegalArgumentException("For the month " + monthToString(this.month) + " the day must be 1-31");
            }
        }
    }
    /**
     * tomorrow is a method that when called on an instantiated {@link Date} will return a {@link Date} with tomorrows date.
     * It will roll over the month and even year if the day is the last day of month or year
     * @return Date object that is the day after the {@link Date} this object is called on.
     */
    public Date tomorrow(){
        Date tomorrow = new Date(this);
        try{
            tomorrow.setDay(this.getDay() + 1);
        }catch (IllegalArgumentException e){
            try{
                tomorrow.setDay(1);
                tomorrow.setMonth(this.getMonth() + 1);
            }catch (IllegalArgumentException ex){
                tomorrow.setMonth(1);
                tomorrow.setYear(this.getYear() + 1);
            }
        }
        return tomorrow;
    }

    /**
     * monthToString is a method that can be called on an instantiated {@link Date} and returns the {@link Date}'s month as a String
     * that the method is called on.
     * @return {@link #month} as a String.
     */
    public String monthToString(){
        return monthStrings[month-1];
    }

    /**
     * monthToString(int) method is a static version of the {@link #monthToString()} method. This provides a way to get
     * a String of a month without an Instantiated {@link Date}.
     * @param month month to get as String
     * @return month with the value of the integer passed in as the parameter
     * @throws IllegalArgumentException if the integer provided is not 1-12 since it is not a valid month
     */
    public static String monthToString(int month){
        if(month < 1 || month > 12){
            throw new IllegalArgumentException("Month provided must be a number 1-12");
        }
        return monthStrings[month-1];
    }
    /**
     * dateEquals method is called on an instantiated date and checks if the {@link Date} passed in as a parameter has the same date as it.
     * @param date Date that is compared to the Date object that the method was called on.
     * @return boolean of true of false based on if that Date's are equal
     */
    public boolean dateEquals(Date date){
        if(this.year == date.getYear() && this.month == date.getMonth() && this.day == date.getDay()){
            return true;
        }
        return false;
    }
    /**
     * toString method overwrites {@link Object#toString()} method and returns a string of the date in a pretty format.
     * @return String of the {@link Date}'s date in a pretty format.
     */
    @Override
    public String toString(){
        return this.monthToString() + " " + this.getDay() + ", " + this.getYear();
    }

}
