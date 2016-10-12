package ru.edu.spbstu.game;
import java.util.*;
import java.lang.*;

public class Map {

    public int[][] mapArray = new int[30][40];  //array of tiles
    public int width = 40;
    public int height = 30;
    public Zombie[] zombies;
    public int amountOfZombies;

    public Map() {
        for(int i=0; i<height; ++i)
            for(int j=0;j<width;++j)
                mapArray[i][j] = -1;
    }

    public Map(int width, int height, int tilewidth) {
        mapArray = new int[height/tilewidth][width/tilewidth]; //creating an empty map with custom size
        this.width = width/tilewidth;
        this.height = height/tilewidth;
        for(int i=0; i<this.height; ++i)
            for(int j=0; j<this.width; ++j)
                mapArray[i][j] = -1;
    }

    private class Point implements Comparable<Point>
    {
        public int x = 0;
        public int y = 0;

        Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) {
                return false;
            }
            Point pt = (Point) obj;
            return x == pt.x && y == pt.y;
        }


        public int compareTo(Point a)
        {
            if (this.y < a.y)
                return -1;
            else if (this.y == a.y)
                return this.x - a.x;
            else
                return 1;
        }
    }


    public void generate() {
        Random rand = new Random();
        Point pos = new Point(0, 0);
        PriorityQueue<Point> nextPoint = new PriorityQueue<Point>();
        nextPoint.add(pos);
        Point brush = new Point(0, 0);
        int rectWidth;
        int rectHeight;
        String debug = "";
        while (!(nextPoint.isEmpty()))
        {
            // This cycle is for map debugging
            for(int i=0; i<height; ++i) {
                for (int j = 0; j < width; ++j) {
                    System.err.print(mapArray[i][j]);
                    System.err.print(" ");
                }
                System.err.print("\n");
            }
            System.err.print("\n");

            //Polling new point
            pos = nextPoint.poll();
            while (mapArray[pos.y][pos.x] != -1)
                pos = nextPoint.poll();

            debug = "Polled point: ";
            debug += pos.x;
            debug += " " + pos.y;
            System.err.println(debug);

            //Rectangle size
            rectWidth = 2+rand.nextInt(8);
            rectHeight = 2+rand.nextInt(8);

            //Adjusting rectangle size to map borders;
            if (pos.x + rectWidth >= width -1)
            {
                rectWidth = width - pos.x;
            }
            if (pos.y + rectHeight >= height - 1)
            {
                rectHeight = height - pos.y;
            }

            //Adjusting rectangle size to neighbor blocks; TODO: fix out-of-bordering
            while (mapArray[pos.y][pos.x + rectWidth - 1] != -1)
                --rectWidth;

            //Initializing the brush
            brush.x = pos.x+rectWidth;
            brush.y = pos.y;

            //Inside
            for(int i = 0; i < rectHeight; ++i)
                for(int j = 0; j < rectWidth; ++j)
                    mapArray[i+pos.y][j+pos.x] = 0;

            //Right side
            for (int i=0; i < rectHeight; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0) {
                    ++brush.y;
                    continue;
                }
                mapArray[brush.y][brush.x] = 1;
                ++brush.y;
            }

            //Down side
            for (int i = -1; i < rectWidth; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0) {
                    --brush.x;
                    continue;
                }
                mapArray[brush.y][brush.x] = 1;
                --brush.x;
            }

            //Left side
            for (int i = -1; i < rectHeight; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0) {
                    --brush.y;
                    continue;
                }
                mapArray[brush.y][brush.x] = 1;
                --brush.y;
            }

            //Up side
            for (int i = -1; i < rectWidth; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0) {
                    ++brush.x;
                    continue;
                }
                mapArray[brush.y][brush.x] = 1;
                ++brush.x;
            }

            //Adding next points
            if (pos.x + rectWidth + 1 < width && mapArray[pos.y][pos.x+rectWidth+1] == -1) {

                debug = "Adding point: ";
                debug += (pos.x+rectWidth+1);
                debug += " " + pos.y;
                System.err.println(debug);

                nextPoint.add(new Point(pos.x + rectWidth + 1, pos.y));
            }
            if (pos.y + rectHeight + 1 < height && mapArray[pos.y + rectHeight + 1][pos.x] == -1 ) {

                debug = "Adding point: ";
                debug += pos.x;
                debug += " " + (pos.y+rectHeight+1);
                System.err.println(debug);

                nextPoint.add(new Point(pos.x, pos.y + rectHeight + 1));
            }

        }

    }

    public void addZombies(int n) {
        amountOfZombies = n;
        zombies = new Zombie[n];
        for (int i = 0; i < n; ++i) {
            int x, y, vx, vy;
            Random rand = new Random(13);
            if (rand.nextBoolean()) { //Определяет, у вертикальной или горизонтальной границы появится зомби
                if (rand.nextBoolean()) { //Определяет, с левой или правой вертикальной границы появится зомби, и задает соотв. направление скорости
                    x = 0;
                    vx = 1;
                    vy = 0;
                }
                else {
                    x = width - 1;
                    vx = -1;
                    vy = 0;
                }
                y = rand.nextInt(height); //Выбирает случайную клетку на границе
                while (mapArray[y][x] == 0) //Ищет ближайшую снизу дорогу
                    if (y++ == height)
                        y = 0;
                y = y * 20 + rand.nextInt(20);
            }
            else {
                if (rand.nextBoolean()) { //Определяет, с верхней или нижней границы появится зомби, и задает соотв. направление скорости
                    y = 0;
                    vx = 0;
                    vy = 1;
                }
                else {
                    y = height - 1;
                    vx = 0;
                    vy = -1;
                }
                x = rand.nextInt(width); //Выбирает случайную клетку на границе
                while (mapArray[y][x] != 0) //Ищет ближайшую справа дорогу
                    if (x++ == width)
                        x = 0;
                x = x * 20 + rand.nextInt(20);
            }
            zombies[i] = new Zombie(x, y, vx, vy);
        }
    }

    public void moveZombies() {

        int x, y; //Координаты плитки
        int [] directions = new int [4]; //Массив допустимых направлений движения
        Random rand = new Random();

        for (int i = 0; i < amountOfZombies; ++i) {

            x = zombies[i].getX() / 20; //Координата плитки по оси абсцисс
            y = zombies[i].getY() / 20; //Координата плитки по оси ординат

            if (zombies[i].getPrevCrossingX() != x || zombies[i].getPrevCrossingY() != y) { //Если зомби перешел на новую плитку

                for (int j = 0; j < 4; ++j) { //Находим все возможные пути движения
                    switch (j) {
                        case 0:
                            if (mapArray[x][y + 1] != 0) {
                                directions[j] = 1; //Если путь в данном направлении есть
                            }
                            else {
                                directions[j] = 0; //Если его нет
                            }
                            break;

                        case 1:
                            if (mapArray[x + 1][y] != 0) {
                                directions[j] = 1;
                            }
                            else {
                                directions[j] = 0;
                            }
                            break;

                        case 2:
                            if (mapArray[x][y - 1] != 0) {
                                directions[j] = 1;
                            }
                            else {
                                directions[j] = 0;
                            }
                            break;

                        case 3:
                            if (mapArray[x - 1][y] != 0) {
                                directions[j] = 1;
                            }
                            else {
                                directions[j] = 0;
                            }
                            break;
                    }
                }

                if (Math.abs(zombies[i].getVx()) > Math.abs(zombies[i].getVy())) { //Определяем, откуда пришел зомби
                    directions[2 + zombies[i].getVx()] = 2;
                }
                else {
                    directions[1 + zombies[i].getVx()] = 2;
                }

                int k = rand.nextInt(4); //Выбираем случайное направление движения
                int res = 4;
                for (int l = 0; l < 4; ++l) { //Проверяем его
                    if (directions[k] == 2) { //Если отсюда пришел зомби, запоминаем направленпие и идем дальше в поисках нового и лучшего
                        res = k;
                        k++;
                    }
                    if (directions[k] == 1) { //Если зомби здесь еще не был, бежим навстречу приключениям
                        res = k;
                        break;
                    }
                    if (directions[k] == 0) { //Если сюда нельзя, продолжаем поиск маршрута
                        if (k == 3) {
                            k = 0;
                        }
                        else {
                            k++;
                        }
                    }
                }

                zombies[i].rotate(((res % 2 == 1) ? 2 - res : 0), ((res % 2 == 0) ? 1 - res : 0)); //Поворачиваем зомби
                zombies[i].setPrevCrossing(x, y); //Запоминаем перекресток
            }
            zombies[i].move(); //Двигаем зомби
        }
    }
}
