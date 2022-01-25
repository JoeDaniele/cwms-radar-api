package cwms.radar.data.dao;

import hec.data.RatingException;
import hec.data.cwmsRating.RatingSet;

import java.io.IOException;

@SuppressWarnings("checkstyle:linelength")
public interface RatingDao {
    void create(RatingSet ratingSet) throws IOException, RatingException;

    RatingSet retrieve(String officeId, String specificationId) throws IOException, RatingException;

    String retrieveRatings(String format, String names, String unit, String datum, String office, String start, String end, String timezone);

    void store(RatingSet ratingSet) throws IOException, RatingException;

    void delete(String officeId, String ratingSpecId) throws IOException, RatingException;
}
