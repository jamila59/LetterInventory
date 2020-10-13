//Jamila Aliyeva
//CSE 143 Sahil Unadkat
//Assessment 2 : LetterInventory
//LetterInventory- used to maintain the count of each letter in a given string. 
//The input is a string and the program will determine the numnber of alphabet letters.


public class LetterInventory {
    
    public static final int ENGLISH_ALPHABET = 26;
    private int size;
    private int[] elementData;


    //constucts a LetterInventory for the letters in the given string and counts the amount of times each letter has occured 
    //then places them in the inventory and increases size 
    //param- the string that will be constructed into LetterInventory 
    public LetterInventory(String data)
    {
        elementData = new int[ENGLISH_ALPHABET];
        data = data.toLowerCase();
        

        for(int i = 0; i < data.length(); i++)
        {
            if(Character.isLetter(data.charAt(i)))
            {
                size++;
                elementData[data.charAt(i) - 'a']++;
            }
        }
    }

    //returns the sum of all the counts in the inventory
    public int size()
    {
        return size;
    }

    //return true is inventory is empty and false is inventory is not empty 
    public boolean isEmpty()
    {
        return size == 0;
    }

    //returns an alphabetically organized version of the inventory in square brackets
    public String toString()
    {
        String result = "[";
        for(int i = 0; i < ENGLISH_ALPHABET; i++)
        {
            for(int j = 0; j < this.elementData[i]; j++)
            {
                result += (char) ('a' + i);
            }
        }
        return result + "]";
    }

    //param- the desiered to be searched for in the inventory
    //returns the count of how many of the desiered letter is in the inventory 
    //thows IllegalArgumentException if nonalphaberic character is passes 
    public int get(char letter)
    {
        if(!Character.isLetter(letter))
        {
            throw new IllegalArgumentException();
        }
        
        return elementData[Character.toLowerCase(letter) - 'a'];
    }

    //Throws IllegalArgumentException is a nonalphaberic character was passed of if values is negative 
    // sets the count for the given letter to the given value
    public void set(char letter, int value)
    {
        if(!Character.isLetter(letter) || value < 0)
        {
            throw new IllegalArgumentException();
        }
        letter = Character.toLowerCase(letter);
        size -= elementData[letter - 'a'];
        elementData[letter - 'a'] = value;

        size += value;
    }

    //param- the other LetterInventory that is being concatenated to the first LetterInventory object
    //construcs and returns a new LetterInventory object that represents the sum of this letter inventory and the other given LetterInventory
    //The counts for each letter are added together 
    public LetterInventory add(LetterInventory other)
    {
        LetterInventory resultAdd = new LetterInventory("");
        for(int i = 0; i< ENGLISH_ALPHABET; i++)
        {
            resultAdd.elementData[i] = this.elementData[i] + other.elementData[i];
        }
        resultAdd.size = this.size() + other.size();
        return resultAdd;
    }
    
    ////param- the other LetterInventory that is being subtract to the first LetterInventory object
    //Constructs and returns a new LetterInventory object that represents the result of subtracting the other inventory from this inventory
    //Returns null if the resulting count is negative
    public LetterInventory subtract(LetterInventory other)
    {
        LetterInventory resultSub = new LetterInventory("");
        for(int i = 0; i < ENGLISH_ALPHABET; i++)
        {
            if((this.elementData[i] - other.elementData[i]) < 0)
            {
                return resultSub =null;
            }
            resultSub.elementData[i] = this.elementData[i] - other.elementData[i];
        }
       resultSub.size = this.size() - other.size();
       return resultSub;
    }


    public static void main(String[] args) {
        LetterInventory inventory = new LetterInventory("washington state");
        System.out.println(inventory.get('a'));
    }

    // Returns true if the other object stores the same character counts as this inventory.
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LetterInventory)) {
            return false;
        }
        LetterInventory other = (LetterInventory) o;
        LetterInventory diff = this.subtract(other);
        return diff != null && diff.isEmpty();
    }

    // Returns a hash code value for this letter inventory.
    public int hashCode() {
        int result = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            result += c * get(c);
        }
        return result;
    }

    // Returns the cosine similarity between this inventory and the other inventory.
    public double similarity(LetterInventory other) {
        long product = 0;
        double thisNorm = 0;
        double otherNorm = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            int a = this.get(c);
            int b = other.get(c);
            product += a * (long) b;
            thisNorm += a * a;
            otherNorm += b * b;
        }
        if (thisNorm <= 0 || otherNorm <= 0) {
            return 0;
        }
        return product / (Math.sqrt(thisNorm) * Math.sqrt(otherNorm));
    }
}
