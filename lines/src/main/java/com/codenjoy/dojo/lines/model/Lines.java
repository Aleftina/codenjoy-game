package com.codenjoy.dojo.lines.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.lines.model.items.Ball;
import com.codenjoy.dojo.lines.services.Events;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.toList;

/**
 * О! Это самое сердце игры - борда, на которой все происходит.
 * Если какой-то из жителей борды вдруг захочет узнать что-то у нее,
 * то лучше ему дать интефейс {@see Field}
 * Борда реализует интерфейс {@see Tickable} чтобы быть уведомленной о
 * каждом тике игры. Обрати внимание на {Sample#tick()}
 */
public class Lines implements Field {

    private List<Ball> balls;

    private List<Player> players;

    private final int size;
    private Dice dice;

    public Lines(Level level, Dice dice) {
        this.dice = dice;
        size = level.getSize();
        players = new LinkedList<>();
        balls = new LinkedList<>();
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        for (Player player : players) {
            Hero hero = player.getHero();

            hero.tick();

//            if (gold.contains(hero)) {
//                gold.remove(hero);
//                player.event(Events.WIN);
//
//                Point pos = getFreeRandom();
//                gold.add(new Gold(pos));
//            }
        }

        for (Player player : players) {
            Hero hero = player.getHero();

            if (!hero.isAlive()) {
                player.event(Events.LOOSE);
            }
        }
    }

    public int size() {
        return size;
    }

    @Override
    public Point getFreeRandom() {
        int x;
        int y;
        int c = 0;
      //  do {
            x = dice.next(size);
            y = dice.next(size);
        //} while (!isFree(x, y) && c++ < 100);

        if (c >= 100) {
            return pt(0, 0);
        }

        return pt(x, y);
    }

//    @Override
//    public boolean isFree(int x, int y) {
//        pt = pt(x, y);
//
//        return !(pt.gold.contains(pt)
//                || getHeroes().contains(pt));
//    }
//
//    @Override
//    public boolean isBomb(int x, int y) {
//        return balls.contains(pt(x, y));
//    }

    @Override
    public void setBall(Elements color, int x, int y) {
        Point pt = pt(x, y);
        if (!balls.contains(pt)) {
            balls.add(new Ball(color, x, y));
        }
    }

    @Override
    public void removeBall(int x, int y) {
        balls.remove(pt(x, y));
    }


    @Override
    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    @Override
    public void remove(Player player) {

    }

    public List<Ball> getBalls() {
        return balls;
    }

    @Override
    public BoardReader reader() {
        return new BoardReader() {
            private int size = Lines.this.size;

            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterable<? extends Point> elements() {
                return new LinkedList<Point>(){{
                    addAll(Lines.this.getBalls());
                }};
            }
        };
    }
}
