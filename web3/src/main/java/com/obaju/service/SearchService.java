package com.obaju.service;

import com.obaju.model.Product;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class SearchService {
    @PersistenceContext
    private EntityManager entityManager;

    //perform indexing on table
    public void intializeHibernateSearch() {
        try {
             FullTextEntityManager fullTextEntityManager =
                    Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public List<Product> search(String keyword, boolean reverse){
        intializeHibernateSearch();
        FullTextEntityManager fullTextEntityManager =
                Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb =
                fullTextEntityManager.getSearchFactory().buildQueryBuilder()
                        .forEntity(Product.class).get();
        Query luceneQuery = qb.keyword().wildcard().onFields("name", "description")
                .matching(keyword).createQuery();
        org.apache.lucene.search.Sort sort = new Sort(
                new SortField("price", SortField.Type.DOUBLE, reverse));
        javax.persistence.Query jpaQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery,Product.class);
        ((FullTextQuery) jpaQuery).setSort(sort);
        List<Product> productList = jpaQuery.getResultList();
        return productList;
    }
}