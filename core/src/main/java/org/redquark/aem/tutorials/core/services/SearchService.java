package org.redquark.aem.tutorials.core.services;

import java.util.List;

public interface SearchService {

    List<String> searchByKeyword(String keyword);
}
