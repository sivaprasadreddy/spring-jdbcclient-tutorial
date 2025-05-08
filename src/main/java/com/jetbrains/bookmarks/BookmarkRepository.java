package com.jetbrains.bookmarks;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class BookmarkRepository {
    private final JdbcClient jdbcClient;

    public BookmarkRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Bookmark> findAll() {
        String sql = "select id, title, url, created_at from bookmarks";
        //return jdbcClient.sql(sql).query(Bookmark.class).list();
        return jdbcClient.sql(sql).query(new BookmarkRowMapper()).list();
    }

    public Optional<Bookmark> findById(Long id) {
        String sql = "select id, title, url, created_at from bookmarks where id = :id";
        return jdbcClient.sql(sql).param("id", id).query(Bookmark.class).optional();
    }

    public Long save(Bookmark bookmark) {
        String sql = """
                     insert into bookmarks(title, url, created_at) 
                     values(:title,:url,:createdAt)
                     """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .param("title", bookmark.title())
                .param("url", bookmark.url())
                .param("createdAt", Timestamp.from(bookmark.createdAt()))
                .update(keyHolder);
        //return keyHolder.getKeyAs(Long.class);
        return Long.parseLong(keyHolder.getKeys().get("ID").toString());
    }

    public void update(Bookmark bookmark) {
        String sql = "update bookmarks set title = :title, url = :url where id = :id";
        int noOfRowsUpdated = jdbcClient.sql(sql)
                .param("title", bookmark.title())
                .param("url", bookmark.url())
                .param("id", bookmark.id())
                .update();
        if (noOfRowsUpdated == 0) {
            throw new RuntimeException("Bookmark not found with id: " + bookmark.id());
        }
    }

    public void delete(Long id) {
        String sql = "delete from bookmarks where id = ?";
        jdbcClient.sql(sql).param(1, id).update();
    }

    static class BookmarkRowMapper implements RowMapper<Bookmark> {
        @Override
        public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Bookmark(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getTimestamp("created_at").toInstant()
            );
        }
    }
}
