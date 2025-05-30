package com.MTGCollectionApp.demo.dao;
import com.MTGCollectionApp.demo.entity.DatabaseCard;
import java.util.List;

/**
 * CRUD interface for accessing DatabaseCard in local database
 *
 * @author timmonsevan
 */

public interface CardDAO {

    void save(DatabaseCard theDatabaseCard);

    DatabaseCard findById(Long id);

    List<DatabaseCard> findAll();

    List<DatabaseCard> findByName(String name);

    void update(DatabaseCard theDatabaseCard);

    void delete(String name);

    void delete(long id);
}
