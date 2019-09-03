package com.emarsys.predict.api.model;

import com.emarsys.testUtil.TimeoutUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProductTest {

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String LINK_URL = "https://emarsys.com";
    private static final String FEATURE = "feature";
    private static final String COHORT = "AAAA";
    private static final Map<String, String> CUSTOM_FIELDS = new HashMap<>();
    private static final String IMAGE_URL = "https://emarsys.com";
    private static final String CATEGORY_PATH = "category path";
    private static final String PRODUCT_DESCRIPTION = "product description";
    private static final Float PRICE = 2.0f;
    private static final String ALBUM = "album";
    private static final String ACTOR = "actor";
    private static final String ARTIST = "artist";
    private static final String AUTHOR = "author";
    private static final String BRAND = "brand";
    private static final Integer YEAR = 1234;

    @Rule
    public TestRule timeout = TimeoutUtils.getTimeoutRule();

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_productId_mustNotBeNull() {
        new Product.Builder(null, TITLE, LINK_URL, FEATURE, COHORT).customFields(CUSTOM_FIELDS).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_title_mustNotBeNull() {
        new Product.Builder(ID, null, LINK_URL, FEATURE, COHORT).customFields(CUSTOM_FIELDS).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_linkUrl_mustNotBeNull() {
        new Product.Builder(ID, TITLE, null, FEATURE, COHORT).customFields(CUSTOM_FIELDS).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_feature_mustNotBeNull() {
        new Product.Builder(ID, TITLE, LINK_URL, null, COHORT).customFields(CUSTOM_FIELDS).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_cohort_mustNotBeNull() {
        new Product.Builder(ID, TITLE, LINK_URL, FEATURE, null).customFields(CUSTOM_FIELDS).build();
    }

    @Test
    public void testBuilder_customFields_shouldInitWithEmptyMap_insteadOfNull() {
        Product product = new Product.Builder(ID, TITLE, LINK_URL, FEATURE, COHORT).build();

        assertNotNull(product.getCustomFields());
    }

    @Test
    public void testBuilder_mandatoryParameters_areInitialized() throws MalformedURLException {
        Product result = new Product.Builder(ID, TITLE, LINK_URL, FEATURE, COHORT).build();

        assertEquals(ID, result.getProductId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(new URL(LINK_URL), result.getLinkUrl());
        assertEquals(FEATURE, result.getFeature());
    }

    @Test
    public void testBuilder_withAllArguments() {
        Map<String, String> customFields = new HashMap<>();
        customFields.put("customKey", "customValue");

        Product result = new Product.Builder(ID, TITLE, LINK_URL, FEATURE, COHORT)
                .customFields(customFields)
                .imageUrl(IMAGE_URL)
                .zoomImageUrl(IMAGE_URL)
                .categoryPath(CATEGORY_PATH)
                .available(true)
                .productDescription(PRODUCT_DESCRIPTION)
                .price(PRICE)
                .msrp(PRICE)
                .album(ALBUM)
                .actor(ACTOR)
                .artist(ARTIST)
                .author(AUTHOR)
                .brand(BRAND)
                .year(YEAR)
                .build();

        Product expected = new Product(ID, TITLE, LINK_URL, FEATURE, COHORT, customFields, IMAGE_URL, IMAGE_URL, CATEGORY_PATH, true, PRODUCT_DESCRIPTION, PRICE, PRICE, ALBUM, ACTOR, ARTIST, AUTHOR, BRAND, YEAR);

        assertEquals(expected, result);
    }

    @Test
    public void testBuilder_withRequiredArguments() {
        Map<String, String> customFields = new HashMap<>();

        Product result = new Product.Builder(ID, TITLE, LINK_URL, FEATURE, COHORT)
                .build();

        Product expected = new Product(ID, TITLE, LINK_URL, FEATURE, COHORT, customFields, null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertEquals(expected, result);
    }
}