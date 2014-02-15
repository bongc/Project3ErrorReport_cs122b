DELIMITER $$

	DROP PROCEDURE IF EXISTS `add_movie` $$
	CREATE PROCEDURE `add_movie`
	   (IN MOVIE_NAME VARCHAR(255),
	   IN MOVIE_YEAR INT,
	   IN MOVIE_DIRECTOR VARCHAR(255),
	   IN MOVIE_BANNER_URL VARCHAR(255),
	   IN MOVIE_TRAILER_URL VARCHAR(255),
	   IN STAR_FIRST_NAME VARCHAR(255),
	   IN STAR_LAST_NAME VARCHAR(255),
	   IN STAR_DOB DATE,
	   IN STAR_PHOTO_URL VARCHAR(255),
	   IN GENRE_NAME VARCHAR(255))
	BEGIN
	   DECLARE movieExistsCount INT DEFAULT 0;
	   DECLARE starExistsCount INT DEFAULT 0;
	   DECLARE genreExistsCount INT DEFAULT 0;
	   DECLARE starID INT DEFAULT 0;
	   DECLARE movieID INT DEFAULT 0;
	   DECLARE genreID INT DEFAULT 0;
	   
	   SELECT count(*) INTO movieExistsCount FROM MOVIES WHERE title = MOVIE_NAME and year = MOVIE_YEAR and director = MOVIE_DIRECTOR;

	   IF (movieExistsCount = 0) THEN
	      INSERT INTO MOVIES (ID, TITLE, YEAR, DIRECTOR, BANNER_URL, TRAILER_URL) 
		    VALUES (DEFAULT, MOVIE_NAME, MOVIE_YEAR, MOVIE_DIRECTOR, MOVIE_BANNER_URL, MOVIE_TRAILER_URL);
			 
		  SELECT count(*) INTO starExistsCount FROM stars WHERE first_name=STAR_FIRST_NAME AND last_name=STAR_LAST_NAME;
		  IF (starExistsCount = 0) THEN
			INSERT INTO stars (id, first_name, last_name, dob, photo_url)
			  VALUES(DEFAULT, STAR_FIRST_NAME, STAR_LAST_NAME, STAR_DOB, STAR_PHOTO_URL);
		  END IF;
		  
		  SELECT id INTO starID FROM stars WHERE first_name=STAR_FIRST_NAME AND last_name=STAR_LAST_NAME LIMIT 1;
		  SELECT id INTO movieID FROM movies WHERE title=MOVIE_NAME AND year=MOVIE_YEAR AND director=MOVIE_DIRECTOR LIMIT 1;
		  INSERT INTO stars_in_movies (star_id, movie_id)
		    VALUES(starID, movieID);
		
		  SELECT count(*) INTO genreExistsCount FROM genres WHERE name=GENRE_NAME;
		  IF (genreExistsCount = 0) THEN
			INSERT INTO Genres (id, name) VALUES (DEFAULT,GENRE_NAME);
		  END IF;
		  SELECT id INTO genreID FROM genres WHERE name=GENRE_NAME;
		  INSERT INTO genres_in_movies (genre_id, movie_id) VALUES (genreID, movieID);
	   END IF;
	END $$

DELIMITER ;

-- CALL ADD_MOVIE("DONG JU 123", 1992, "DONG JU", NULL, NULL, "DONG JU", "LEE", "1992-04-14", NULL, "Comedy");