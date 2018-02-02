/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.db.helper;

import java.util.List;

public interface IDatabase<T> {
    public void save(List<T> list);

    public void save(T entity);

    public T query(int id);

    public List<T> query();

    public void delete(int id);

    public void delete(String str);

    public void update(T entity);
}
