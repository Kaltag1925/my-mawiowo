package dk.itu.mario.level;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;

public class DavisBrayLevel extends Level implements LevelInterface {

	private Random random = new Random();
	private int type;
	private int floor;
	
	public DavisBrayLevel(int width, int height, long seed, int difficulty, int type, GamePlay playerMetrics) {
		super(width, height);
		floor = height - 2;
		create(seed, difficulty, type);
	}
	
	private void create(long seed, int difficulty, int type)
	{
		this.type = type;
		int length = 0;

		LSystem gen = new LSystem();
		for (int i = 0; i < 4; i ++) {
		    gen.iterate();
        }

        System.out.println(gen.current.size());
        for (Letter le : gen.current) {
            switch (le) {
                case Safe:
                    length += buildFlat(length, 7);
                    break;
                case SingleFlat:
                    length += buildFlat(length, 2);
                    break;
                case Flat:
                    length += buildFlat(length, 5);
                    break;
                case ShortGap:
                    length += 2;
                    break;
                case LongGap:
                    length += 4;
                    break;
                case StairLeft:
                    System.out.println("STRAITS");
                    length += buildLeftStairs(length, 5);
                    break;
                case StairRight:
                    System.out.println("STRAITS");
                    length += buildRightStairs(length, 5);
                    break;
                case Block:
                    buildBlock(length, 2);
                    length += buildFlat(length, 1);
                    break;
                case DoubleBlock:
                    buildBlock(length, 3);
                    buildBlock(length, 6);
                    length += buildFlat(length, 1);
                    break;
                case Pipe:
                    length += buildPipe(length);
                    break;
                case Bowling:
                    int size = random.nextInt(10) + 4;
                    placeBownling(length, size);
                    length += buildFlat(length, size);
                    break;
                case Plant:
                    length += buildPipe(length);
                    break;
                case CoinBlock:
                    length += buildFlat(length, 1);
                    break;
                case CoinRow:
                    placeCoinRow(length);
                    length += buildFlat(length, 5);
                    break;
                case PowerUp:
                    placePowerUp(length);
                    length += buildFlat(length, 1);
                    break;
            }
        }



        //create the exit
        xExit = length;
        yExit = floor;
        
		fixWalls();
	}

    private void placePowerUp(int xo) {
	    setBlock(xo, floor - 3, Level.BLOCK_POWERUP);
    }

    private void placeCoinRow(int xo) {
	    for (int x = 0; x < 5; x++) {
	        setBlock(xo + x, floor - 2, Level.COIN);
        }
    }

    private void placeBownling(int xo, int size) {
	    setSpriteTemplate(xo, floor - 1, new SpriteTemplate(SpriteTemplate.GREEN_TURTLE, false));

	    for (int x = 2; x < size; x++) {
            setSpriteTemplate(xo + x, floor - 1, new SpriteTemplate(SpriteTemplate.GOOMPA, false));
            // Huh I thought they were called Goombas, the only Goompa I know is the one from Paper Mario 64
        }
    }

    private int buildPipe(int xo) {
        int pipeHeight = random.nextInt(4);
        if (floor - pipeHeight < 2) {
            pipeHeight = 0;
        }
        //left side
        setBlock(xo, floor - 1 - pipeHeight, Level.TUBE_TOP_LEFT);
        for (int y = 0; y < height; y++) {
            if (y >= floor - pipeHeight) {
                setBlock(xo, y, Level.TUBE_SIDE_LEFT);
            }
        }
        //right side
        setBlock(xo + 1, floor - 1 - pipeHeight, Level.TUBE_TOP_RIGHT);
        for (int y = 0; y < height; y++) {
            if (y >= floor - pipeHeight) {
                setBlock(xo + 1, y, Level.TUBE_SIDE_RIGHT);
            }
        }

        return 2;
    }


    private int buildFlat(int xo, int length) {
	    for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                if (y >= floor) {
                    setBlock(x + xo, y, Level.GROUND);
                }
            }
        }
	    return length;
    }

    private int buildLeftStairs(int xo, int length) {
        if (floor - 5 > 0) {
            for (int x = 0; x < length - 1; x++) {
                for (int y = 0; y < height; y++) {
                    if (y >= floor - x - 1) {
                        setBlock(x + xo, y, Level.GROUND);
                    }
                }
            }
            floor -= length - 1;
            for (int y = 0; y < height; y++) {
                if (y >= floor) {
                    setBlock(xo + length - 1, y, Level.GROUND);
                }
            }
        } else {
            buildFlat(xo, length);
        }
	    return length;
    }

    private int buildRightStairs(int xo, int length) {
	    if (floor + 5 < height) {
            for (int y = 0; y < height; y++) {
                if (y >= floor) {
                    setBlock(xo, y, Level.GROUND);
                }
            }
            for (int x = 1; x < length; x++) {
                for (int y = 0; y < height; y++) {
                    if (y >= floor + x - 1) {
                        setBlock(x + xo, y, Level.GROUND);
                    }
                }
            }
            floor += length + 1;
        } else {
	        buildFlat(xo, length);
        }
        return length;
    }

    private int buildBlock(int xo, int y) {
	    setBlock(xo, floor - y, Level.BLOCK_EMPTY);
	    return 0;
    }




    private int buildStraight(int xo, int maxLength, boolean safe) {
        int length = random.nextInt(10) + 2;

        if (safe)
            length = 10 + random.nextInt(5);

        if (length > maxLength)
            length = maxLength;

        int floor = height - 1 - random.nextInt(4);

        //runs from the specified x position to the length of the segment
        for (int x = xo; x < xo + length; x++) {
            for (int y = 0; y < height; y++) {
                if (y >= floor) {
                    setBlock(x, y, Level.GROUND);
                }
            }
        }

        return length;
    }
	
    private void fixWalls() {
        boolean[][] blockMap = new boolean[width + 1][height + 1];

        for (int x = 0; x < width + 1; x++) {
            for (int y = 0; y < height + 1; y++) {
                int blocks = 0;
                for (int xx = x - 1; xx < x + 1; xx++) {
                    for (int yy = y - 1; yy < y + 1; yy++) {
                        if (getBlockCapped(xx, yy) == GROUND) {
                            blocks++;
                        }
                    }
                }
                blockMap[x][y] = blocks == 4;
            }
        }
        blockify(this, blockMap, width + 1, height + 1);
    }

    private void blockify(Level level, boolean[][] blocks, int width,
                          int height) {
        int to = 0;
        if (type == LevelInterface.TYPE_CASTLE) {
            to = 4 * 2;
        } else if (type == LevelInterface.TYPE_UNDERGROUND) {
            to = 4 * 3;
        }

        boolean[][] b = new boolean[2][2];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int xx = x; xx <= x + 1; xx++) {
                    for (int yy = y; yy <= y + 1; yy++) {
                        int _xx = xx;
                        int _yy = yy;
                        if (_xx < 0) _xx = 0;
                        if (_yy < 0) _yy = 0;
                        if (_xx > width - 1) _xx = width - 1;
                        if (_yy > height - 1) _yy = height - 1;
                        b[xx - x][yy - y] = blocks[_xx][_yy];
                    }
                }

                if (b[0][0] == b[1][0] && b[0][1] == b[1][1]) {
                    if (b[0][0] == b[0][1]) {
                        if (b[0][0]) {
                            level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                        } else {
                            // KEEP OLD BLOCK!
                        }
                    } else {
                        if (b[0][0]) {
                            //down grass top?
                            level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
                        } else {
                            //up grass top
                            level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
                        }
                    }
                } else if (b[0][0] == b[0][1] && b[1][0] == b[1][1]) {
                    if (b[0][0]) {
                        //right grass top
                        level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
                    } else {
                        //left grass top
                        level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
                    }
                } else if (b[0][0] == b[1][1] && b[0][1] == b[1][0]) {
                    level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                } else if (b[0][0] == b[1][0]) {
                    if (b[0][0]) {
                        if (b[0][1]) {
                            level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
                        } else {
                            level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
                        }
                    } else {
                        if (b[0][1]) {
                            //right up grass top
                            level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
                        } else {
                            //left up grass top
                            level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
                        }
                    }
                } else if (b[0][1] == b[1][1]) {
                    if (b[0][1]) {
                        if (b[0][0]) {
                            //left pocket grass
                            level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
                        } else {
                            //right pocket grass
                            level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
                        }
                    } else {
                        if (b[0][0]) {
                            level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
                        } else {
                            level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
                        }
                    }
                } else {
                    level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
                }
            }
        }
    }


    private enum Letter {
        SingleFlat(1), Flat(5), ShortGap(2), LongGap(5), StairLeft(5), StairRight(5), Block(1), DoubleBlock(1),
        Pipe(2), Bowling(1), Plant(1), PowerUp(1), CoinBlock(1), CoinRow(1), Safe(5);
        int length;

        private Letter(int length) {
            this.length = length;
        }
    }

    public class LSystem {

        Rule[] rules = {new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.Flat}, .2),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.LongGap, Letter.Flat}, .5),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.ShortGap, Letter.Flat, Letter.ShortGap, Letter.Flat}, 0),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.StairLeft, Letter.LongGap, Letter.StairRight, Letter.Flat}, .4),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.SingleFlat, Letter.Block, Letter.Block, Letter.Block, Letter.SingleFlat, Letter.Flat}, .3),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.Bowling, Letter.Flat}, .4),
                new Rule(Letter.LongGap, new Letter[] {Letter.LongGap, Letter.SingleFlat, Letter.SingleFlat, Letter.SingleFlat, Letter.LongGap}, .3),
                new Rule(Letter.StairLeft, new Letter[] {Letter.StairLeft, Letter.Pipe, Letter.StairRight}, .4),
                new Rule(Letter.StairRight, new Letter[] {Letter.StairLeft, Letter.Pipe, Letter.StairRight}, .4),
                new Rule(Letter.Pipe, new Letter[] {Letter.Plant}, .5),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.StairLeft}, .45),
                new Rule(Letter.Flat, new Letter[] {Letter.StairRight, Letter.Flat}, .3),
                new Rule(Letter.Block, new Letter[] {Letter.DoubleBlock}, .1),
                new Rule(Letter.Block, new Letter[] {Letter.PowerUp}, .3),
                new Rule(Letter.Block, new Letter[] {Letter.CoinBlock}, .5),
                new Rule(Letter.Flat, new Letter[] {Letter.Flat, Letter.CoinRow, Letter.Flat}, .3)};
        ArrayList<Letter> current = new ArrayList<>();

        public LSystem() {
            current.add(Letter.Safe);
            current.add(Letter.Flat);
            
        }

        public void iterate() {
            ArrayList<Letter> newCurrent = new ArrayList<>();
            for(Letter le : current) {
                newCurrent.addAll(findRule(le));

            }
            current = newCurrent;

        }

        private ArrayList<Letter> findRule(Letter le) {
            ArrayList<Rule> possibleRules = new ArrayList<>();
            for (Rule r : rules) {
                if (r.in == le && random.nextDouble() < r.chance) {
                    possibleRules.add(r);
                }
            }

            if (possibleRules.isEmpty()) {
                ArrayList<Letter> ret = new ArrayList<>();
                ret.add(le);
                return ret;
            } else if (possibleRules.size() == 1) {
                return new ArrayList<>(Arrays.asList(possibleRules.get(0).out));
            } else {
                return new ArrayList<>(Arrays.asList(possibleRules.get(random.nextInt(possibleRules.size())).out));
            }
        }

//        private ArrayList<Letter> weightedRandomRule(ArrayList<Rule> possibleRules) {
//            int total = 0;
//            for (Rule r : possibleRules) {
//                total += r.chance * 100;
//            }
//
//            int randomInt = random.nextInt(total);
//            int current = 0;
//            int index = 0;
//            while (index + 1 < possibleRules.size() && current + ((int) possibleRules.get(index).chance) * 100 < randomInt) {
//                index++;
//                current += possibleRules.get(index).chance * 100;
//            }
//
//            return new ArrayList<>(Arrays.asList(possibleRules.get(index).out));
        }
        public class Rule {
            Letter in;
            Letter[] out;
            double chance;

            public Rule(Letter in, Letter[] out, double chance) {
                this.in = in;
                this.out = out;
                this.chance = chance;
            }
        }
    }
