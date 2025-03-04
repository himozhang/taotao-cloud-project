package com.taotao.cloud.common.support.generator;


import com.taotao.cloud.common.support.generator.base.GenericGenerator;

public class EnglishNameGenerator extends GenericGenerator {
    static String[] FIRST_NAMES = {
        "Aaron",
        "Abel",
        "Abraham",
        "Adam",
        "Adrian",
        "Aidan",
        "Alva",
        "Alex",
        "Alexander",
        "Alan",
        "Albert",
        "Alfred",
        "Andrew",
        "Andy",
        "Angus",
        "Anthony",
        "Apollo",
        "Arnold",
        "Arthur",
        "August",
        "Austin",
        "Ben",
        "Benjamin",
        "Bert",
        "Benson",
        "Bill",
        "Billy",
        "Blake",
        "Bob",
        "Bobby",
        "Brad",
        "Brandon",
        "Brant",
        "Brent",
        "Brian",
        "Brown",
        "Bruce",
        "Caleb",
        "Cameron",
        "Carl",
        "Carlos",
        "Cary",
        "Caspar",
        "Cecil",
        "Charles",
        "Cheney",
        "Chris",
        "Christian",
        "Christopher",
        "Clark",
        "Cliff",
        "Cody",
        "Cole",
        "Colin",
        "Cosmo",
        "Daniel",
        "Denny",
        "Darwin",
        "David",
        "Dennis",
        "Derek",
        "Dick",
        "Donald",
        "Douglas",
        "Duke",
        "Dylan",
        "Eddie",
        "Edgar",
        "Edison",
        "Edmund",
        "Edward",
        "Edwin",
        "Elijah",
        "Elliott",
        "Elvis",
        "Eric",
        "Ethan",
        "Eugene",
        "Evan",
        "Enterprise",
        "Ford",
        "Francis",
        "Frank",
        "Franklin",
        "Fred",
        "Gabriel",
        "Gaby",
        "Garfield",
        "Gary",
        "Gavin",
        "Geoffrey",
        "George",
        "Gino",
        "Glen",
        "Glendon",
        "Hank",
        "Hardy",
        "Harrison",
        "Harry",
        "Hayden",
        "Henry",
        "Hilton",
        "Hugo",
        "Hunk",
        "Howard",
        "Henry",
        "Ian",
        "Ignativs",
        "Ivan",
        "Isaac",
        "Isaiah",
        "Jack",
        "Jackson",
        "Jacob",
        "James",
        "Jason",
        "Jay",
        "Jeffery",
        "Jerome",
        "Jerry",
        "Jesse",
        "Jim",
        "Jimmy",
        "Joe",
        "John",
        "Johnny",
        "Jonathan",
        "Jordan",
        "Jose",
        "Joshua",
        "Justin",
        "Keith",
        "Ken",
        "Kennedy",
        "Kenneth",
        "Kenny",
        "Kevin",
        "Kyle",
        "Lance",
        "Larry",
        "Laurent",
        "Lawrence",
        "Leander",
        "Lee",
        "Leo",
        "Leonard",
        "Leopold",
        "Leslie",
        "Loren",
        "Lori",
        "Lorin",
        "Louis",
        "Luke",
        "Marcus",
        "Marcy",
        "Mark",
        "Marks",
        "Mars",
        "Marshal",
        "Martin",
        "Marvin",
        "Mason",
        "Matthew",
        "Max",
        "Michael",
        "Mickey",
        "Mike",
        "Nathan",
        "Nathaniel",
        "Neil",
        "Nelson",
        "Nicholas",
        "Nick",
        "Noah",
        "Norman",
        "Oliver",
        "Oscar",
        "Owen",
        "Patrick",
        "Paul",
        "Peter",
        "Philip",
        "Phoebe",
        "Quentin",
        "Randall",
        "Randolph",
        "Randy",
        "Ray",
        "Raymond",
        "Reed",
        "Rex",
        "Richard",
        "Richie",
        "Riley",
        "Robert",
        "Robin",
        "Robinson",
        "Rock",
        "Roger",
        "Ronald",
        "Rowan",
        "Roy",
        "Ryan",
        "Sam",
        "Sammy",
        "Samuel",
        "Scott",
        "Sean",
        "Shawn",
        "Sidney",
        "Simon",
        "Solomon",
        "Spark",
        "Spencer",
        "Spike",
        "Stanley",
        "Steve",
        "Steven",
        "Stewart",
        "Stuart",
        "Terence",
        "Terry",
        "Ted",
        "Thomas",
        "Tim",
        "Timothy",
        "Todd",
        "Tommy",
        "Tom",
        "Thomas",
        "Tony",
        "Tyler",
        "Ultraman",
        "Ulysses",
        "Van",
        "Vern",
        "Vernon",
        "Victor",
        "Vincent",
        "Warner",
        "Warren",
        "Wayne",
        "Wesley",
        "William",
        "Willy",
        "Zack",
        "Zachary",
        "Abigail",
        "Abby",
        "Ada",
        "Adelaide",
        "Adeline",
        "Alexandra",
        "Ailsa",
        "Aimee",
        "Alexis",
        "Alice",
        "Alicia",
        "Alina",
        "Allison",
        "Alyssa",
        "Amanda",
        "Amy",
        "Amber",
        "Anastasia",
        "Andrea",
        "Angel",
        "Angela",
        "Angelia",
        "Angelina",
        "Ann",
        "Anna",
        "Anne",
        "Annie",
        "Anita",
        "Ariel",
        "April",
        "Ashley",
        "Audrey",
        "Aviva",
        "Barbara",
        "Barbie",
        "Beata",
        "Beatrice",
        "Becky",
        "Bella",
        "Bess",
        "Bette",
        "Betty",
        "Blanche",
        "Bonnie",
        "Brenda",
        "Brianna",
        "Britney",
        "Brittany",
        "Camille",
        "Candice",
        "Candy",
        "Carina",
        "Carmen",
        "Carol",
        "Caroline",
        "Carry",
        "Carrie",
        "Cassandra",
        "Cassie",
        "Catherine",
        "Cathy",
        "Chelsea",
        "Charlene",
        "Charlotte",
        "Cherry",
        "Cheryl",
        "Chloe",
        "Chris",
        "Christina",
        "Christine",
        "Christy",
        "Cindy",
        "Claire",
        "Claudia",
        "Clement",
        "Cloris",
        "Connie",
        "Constance",
        "Cora",
        "Corrine",
        "Crystal",
        "Daisy",
        "Daphne",
        "Darcy",
        "Dave",
        "Debbie",
        "Deborah",
        "Debra",
        "Demi",
        "Diana",
        "Dolores",
        "Donna",
        "Dora",
        "Doris",
        "Edith",
        "Editha",
        "Elaine",
        "Eleanor",
        "Elizabeth",
        "Ella",
        "Ellen",
        "Ellie",
        "Emerald",
        "Emily",
        "Emma",
        "Enid",
        "Elsa",
        "Erica",
        "Estelle",
        "Esther",
        "Eudora",
        "Eva",
        "Eve",
        "Evelyn",
        "Fannie",
        "Fay",
        "Fiona",
        "Flora",
        "Florence",
        "Frances",
        "Frederica",
        "Frieda",
        "Flta",
        "Gina",
        "Gillian",
        "Gladys",
        "Gloria",
        "Grace",
        "Grace",
        "Greta",
        "Gwendolyn",
        "Hannah",
        "Haley",
        "Hebe",
        "Helena",
        "Hellen",
        "Henna",
        "Heidi",
        "Hillary",
        "Ingrid",
        "Isabella",
        "Ishara",
        "Irene",
        "Iris",
        "Ivy",
        "Jacqueline",
        "Jade",
        "Jamie",
        "Jane",
        "Janet",
        "Jasmine",
        "Jean",
        "Jenna",
        "Jennifer",
        "Jenny",
        "Jessica",
        "Jessie",
        "Jill",
        "Joan",
        "Joanna",
        "Jocelyn",
        "Joliet",
        "Josephine",
        "Josie",
        "Joy",
        "Joyce",
        "Judith",
        "Judy",
        "Julia",
        "Juliana",
        "Julie",
        "June",
        "Karen",
        "Karida",
        "Katherine",
        "Kate",
        "Kathy",
        "Katie",
        "Katrina",
        "Kay",
        "Kayla",
        "Kelly",
        "Kelsey",
        "Kimberly",
        "Kitty",
        "Lareina",
        "Lassie",
        "Laura",
        "Lauren",
        "Lena",
        "Lydia",
        "Lillian",
        "Lily",
        "Linda",
        "lindsay",
        "Lisa",
        "Liz",
        "Lora",
        "Lorraine",
        "Louisa",
        "Louise",
        "Lucia",
        "Lucy",
        "Lucine",
        "Lulu",
        "Lydia",
        "Lynn",
        "Mabel",
        "Madeline",
        "Maggie",
        "Mamie",
        "Manda",
        "Mandy",
        "Margaret",
        "Mariah",
        "Marilyn",
        "Martha",
        "Mavis",
        "Mary",
        "Matilda",
        "Maureen",
        "Mavis",
        "Maxine",
        "May",
        "Mayme",
        "Megan",
        "Melinda",
        "Melissa",
        "Melody",
        "Mercedes",
        "Meredith",
        "Mia",
        "Michelle",
        "Milly",
        "Miranda",
        "Miriam",
        "Miya",
        "Molly",
        "Monica",
        "Morgan",
        "Nancy",
        "Natalie",
        "Natasha",
        "Nicole",
        "Nikita",
        "Nina",
        "Nora",
        "Norma",
        "Nydia",
        "Octavia",
        "Olina",
        "Olivia",
        "Ophelia",
        "Oprah",
        "Pamela",
        "Patricia",
        "Patty",
        "Paula",
        "Pauline",
        "Pearl",
        "Peggy",
        "Philomena",
        "Phoebe",
        "Phyllis",
        "Polly",
        "Priscilla",
        "Quentina",
        "Rachel",
        "Rebecca",
        "Regina",
        "Rita",
        "Rose",
        "Roxanne",
        "Ruth",
        "Sabrina",
        "Sally",
        "Sandra",
        "Samantha",
        "Sami",
        "Sandra",
        "Sandy",
        "Sarah",
        "Savannah",
        "Scarlett",
        "Selma",
        "Selina",
        "Serena",
        "Sharon",
        "Sheila",
        "Shelley",
        "Sherry",
        "Shirley",
        "Sierra",
        "Silvia",
        "Sonia",
        "Sophia",
        "Stacy",
        "Stella",
        "Stephanie",
        "Sue",
        "Sunny",
        "Susan",
        "Tamara",
        "Tammy",
        "Tanya",
        "Tasha",
        "Teresa",
        "Tess",
        "Tiffany",
        "Tina",
        "Tonya",
        "Tracy",
        "Ursula",
        "Vanessa",
        "Venus",
        "Vera",
        "Vicky",
        "Victoria",
        "Violet",
        "Virginia",
        "Vita",
        "Vivian"
    };
    private static EnglishNameGenerator instance = new EnglishNameGenerator();

    private EnglishNameGenerator() {
    }

    public static EnglishNameGenerator getInstance() {
        return instance;
    }

    @Override
    public String generate() {
        //英文名
        return genFirstName();
    }

    private String genFirstName() {
        return FIRST_NAMES[getRandomInstance().nextInt(FIRST_NAMES.length)];
    }

}
