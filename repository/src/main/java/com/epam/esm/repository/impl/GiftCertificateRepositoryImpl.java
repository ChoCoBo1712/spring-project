package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

  @PersistenceContext private final EntityManager entityManager;

  public GiftCertificateRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<GiftCertificate> filter(
      String[] tagNames, String name, String description, String[] sort) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GiftCertificate> criteriaQuery =
        criteriaBuilder.createQuery(GiftCertificate.class);
    Root<GiftCertificate> certificateRoot = criteriaQuery.from(GiftCertificate.class);

    if (tagNames != null) {
      Predicate tagNamePredicate = getTagNamePredicate(tagNames, criteriaBuilder, certificateRoot);
      criteriaQuery.where(tagNamePredicate);
    }
    if (name != null) {
      Predicate namePredicate = criteriaBuilder.like(certificateRoot.get("name"), "%" + name + "%");
      criteriaQuery.where(namePredicate);
    }
    if (description != null) {
      Predicate descriptionPredicate =
          criteriaBuilder.like(certificateRoot.get("description"), "%" + description + "%");
      criteriaQuery.where(descriptionPredicate);
    }
    if (sort != null) {
      List<Order> orders = buildSortOrders(sort, criteriaBuilder, certificateRoot);
      criteriaQuery.orderBy(orders);
    }

    TypedQuery<GiftCertificate> query = entityManager.createQuery(criteriaQuery);
    return query.getResultList();
  }

  @Override
  public Optional<GiftCertificate> findById(long id) {
    GiftCertificate certificate = entityManager.find(GiftCertificate.class, id);
    return Optional.ofNullable(certificate);
  }

  @Override
  public GiftCertificate create(GiftCertificate certificate) {
    entityManager.persist(certificate);
    return certificate;
  }

  @Override
  public GiftCertificate update(GiftCertificate certificate) {
    return entityManager.merge(certificate);
  }

  @Override
  public void delete(GiftCertificate certificate) {
    entityManager.remove(certificate);
  }

  private Predicate getTagNamePredicate(Object[] tagNames, CriteriaBuilder criteriaBuilder, Root<GiftCertificate> certificateRoot) {
    CriteriaQuery<Tag> tagCriteriaQuery = criteriaBuilder.createQuery(Tag.class);
    Root<Tag> tagRoot = tagCriteriaQuery.from(Tag.class);
    tagCriteriaQuery.where(tagRoot.get("name").in(tagNames));
    List<Tag> tags = entityManager.createQuery(tagCriteriaQuery).getResultList();

    Expression<Set<Tag>> certificateTagNames = certificateRoot.get("tags");
    Predicate tagNamePredicate = criteriaBuilder.conjunction();
    for (Tag tag : tags) {
      tagNamePredicate =
              criteriaBuilder.and(
                      tagNamePredicate, criteriaBuilder.isMember(tag, certificateTagNames));
    }
    return tagNamePredicate;
  }

  private List<Order> buildSortOrders(
      String[] sort, CriteriaBuilder criteriaBuilder, Root<GiftCertificate> certificate) {
    List<Order> orders = new ArrayList<>();

    for (String sortParam : sort) {
      String[] paramOrderPair = sortParam.split("\\.");
      Order order =
          Objects.equals(paramOrderPair[1], "asc")
              ? criteriaBuilder.asc(certificate.get(paramOrderPair[0]))
              : criteriaBuilder.desc(certificate.get(paramOrderPair[0]));
      orders.add(order);
    }

    return orders;
  }
}
