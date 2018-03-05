package java8.defaults;

import java.awt.Point;

interface Figure2 {
    int getX();
    int getY();

    default Point getPoint() {
        return new Point(getX(), getY());
    }
}
