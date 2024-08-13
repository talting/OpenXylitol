package cc.xylitol.utils;

import java.util.Arrays;

public class RandomNameGenerator {

    private final String[] adjectives = {"Courageous", "Valiant", "Quick", "Invisible", "Silent", "Lethal",
            "Pixelated", "Mining", "Crafting", "Exploring", "Building", "Ender", "Sneaky", "Powerful", "Speedy",
            "Glowing", "Unstoppable", "Mysterious", "Fearless", "Dynamic", "Swift", "Stealthy", "Fierce", "Mighty",
            "Enduring", "Terrific", "Shimmering", "Bursting", "Thrilling", "Astounding", "Resilient", "Daring",
            "Majestic", "Adventurous", "Defiant", "Wise", "Fiery", "Frozen", "Brave", "Thunderous", "Lightning",
            "Aquatic", "Arctic", "Arid", "Boisterous", "Bold", "Bountiful", "Capable", "Charming", "Cheerful",
            "Clever", "Comical", "Conscientious", "Cosmic", "Cryptic", "Dazzling", "Deep", "Determined",
            "Eager", "Ecstatic", "Electric", "Enthusiastic", "Exotic", "Extravagant", "Fantastical", "Ferocious",
            "Flamboyant", "Freezing", "Galloping", "Gargantuan", "Gigantic", "Glistening", "Harmonious", "Heroic",
            "Hilarious", "Impulsive", "Incredible", "Ingenious", "Inquisitive", "Insightful", "Inspiring", "Intrepid",
            "Inventive", "Jubilant", "Lucky", "Luminous", "Magical", "Majestic", "Marvellous", "Masterful", "Mythic",
            "Nimble", "Noble", "Observant", "Outstanding", "Phenomenal", "Pioneering", "Playful", "Pristine",
            "Radiant", "Rebellious", "Remarkable", "Resourceful", "Revolutionary", "Ruthless", "Savage",
            "Serpentine", "Spectacular", "Spontaneous", "Stellar", "Stubborn", "Stupendous", "Supernatural",
            "Tenacious", "Titanic", "Torrential", "Tranquil", "Tropical", "Unyielding", "Vigilant", "Vivacious",
            "Volcanic", "Whimsical", "Wild", "Witty", "Wondrous", "Youthful", "Zealous"};

    private final String[] nouns = {"Tiger", "Ninja", "Ghost", "Dragon", "Lion", "Puma", "Creeper", "Zombie",
            "Enderman", "Villager", "Steve", "Pigman", "Skeleton", "Spider", "Wither", "Blaze", "Ghast", "Dolphin",
            "Wolf", "Bear", "Eagle", "Golem", "Phantom", "Rabbit", "Fox", "Panda", "Turtle", "Parrot", "Penguin",
            "Unicorn", "Archer", "Assassin", "Badger", "Bat", "Bee", "Beetle", "Bison", "Boar", "Buffalo",
            "Butterfly", "Cactus", "Camel", "Cat", "Cheetah", "Chimpanzee", "Clam", "Clownfish", "Cobra",
            "Cockroach", "Coyote", "Crab", "Cricket", "Crocodile", "Dove", "Duck", "Eel", "Elephant", "Elk",
            "Falcon", "Ferret", "Flamingo", "Frog", "Giraffe", "Goose", "Gorilla", "Grasshopper", "Grizzly",
            "Hawk", "Hippopotamus", "Hornet", "Hummingbird", "Husky", "Hyena", "Iguana", "Impala", "Jaguar",
            "Jellyfish", "Kangaroo", "Koala", "Kraken", "Leopard", "Llama", "Lobster", "Lynx", "Mammoth", "Monkey",
            "Moose", "Narwhal", "Octopus", "Ostrich", "Otter", "Owl", "Peacock", "Penguin", "Phoenix", "Piranha",
            "Polarbear", "Python", "Quail", "Raccoon", "Ram", "Rat", "Raven", "Rhino", "Salmon", "Scorpion",
            "Seahorse", "Seal", "Shark", "Snail", "Snake", "Snowman", "Squid", "Squirrel", "Stingray", "Tarantula",
            "Toucan", "Vampire", "Viper", "Vulture", "Warrior", "Weasel", "Werewolf", "Whale", "Witch", "Wizard",
            "Wombat", "Worm", "Yak", "Zebra"};

    private final String[] verbs = {"Rush", "Strike", "Shadow", "Climb", "Roar", "Growl", "Dig", "Craft",
            "Build", "Explore", "Enchant", "Brew", "Sprint", "Charge", "Hide", "Leap", "Smash", "Summon", "Burst",
            "Outrun", "Dive", "Glare", "Thrust", "Bounce", "Howl", "Fly", "Pounce", "Crawl", "Swoop", "Stalk",
            "Accelerate", "Ascend", "Attack", "Balance", "Barrage", "Battle", "Blaze", "Blink", "Blossom", "Bounce",
            "Break", "Capture", "Charge", "Charm", "Chase", "Climb", "Collide", "Command", "Conquer", "Creep",
            "Crush", "Dance", "Dash", "Defend", "Descend", "Destroy", "Dominate", "Drift", "Echo", "Enchant",
            "Evade", "Explode", "Flap", "Float", "Glide", "Grasp", "Hover", "Ignite", "Illuminate", "Invade", "Jump",
            "Kick", "Knock", "Lift", "March", "Navigate", "Obliterate", "Observe", "Overwhelm", "Parade", "Patrol",
            "Pierce", "Plunge", "Prance", "Prowl", "Pulverize", "Punch", "Pursue", "Race", "Rage", "Rampage", "Rattle",
            "Rebound", "Rescue", "Resurrect", "Retreat", "Revolve", "Roar", "Rumble", "Scorch", "Shake", "Shatter",
            "Shock", "Shred", "Slam", "Slide", "Slither", "Smack", "Snatch", "Soar", "Stomp", "Storm", "Stride",
            "Struggle", "Surprise", "Survive", "Tackle", "Tease", "Threaten", "Throw", "Thrust", "Topple", "Trample",
            "Trap", "Tremble", "Tumble", "Twirl", "Vanish", "Wander", "Wobble", "Zap"};

    private String randomFromArray(String[] arr) {
        return arr[(int) (Math.random() * arr.length)];
    }

    private String randomCase(String word) {
        return Math.random() > 0.5 ? word.toUpperCase() : word.toLowerCase();
    }

    public String generateRandomName() {
        int tokens = 0;
        StringBuilder nameBuilder = new StringBuilder();
        while (tokens < 12) {
            boolean addUnderline = Math.random() > 0.7 && tokens + 1 <= 11;
            String[] selectedComponent = new String[]{randomFromArray(new String[]{Arrays.toString(adjectives), Arrays.toString(nouns), Arrays.toString(verbs)})};

            String word = randomCase(randomFromArray(selectedComponent));
            if (word.length() + tokens + (addUnderline ? 1 : 0) <= 12) {
                nameBuilder.append(word);
                tokens += word.length();
                if (addUnderline) {
                    nameBuilder.append('_');
                    tokens += 1;
                }
            } else {
                break;
            }
        }

        // Ensure the length is at least 5 tokens
        if (tokens < 5) {
            return "";
        }

        return nameBuilder.toString();
    }

    public void main(String[] args) {
        String randomName = generateRandomName();
        System.out.println(randomName);
    }
}