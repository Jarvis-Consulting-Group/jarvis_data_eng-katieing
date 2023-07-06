# Introduction
In this project, my goal was to establish a comprehensive database for a newly established country club. 
The database includes tables for members, facilities like tennis courts and spas, and booking history for these facilities. 
The database will be utilized by country club managers to analyze facility usage and demand. 
To achieve this, PostgreSQL was used to create the necessary tables and execute various sample 
Data Manipulation Language (DML) queries, seen below, to demonstrate its usage. Sample data was used to verify expected outputs from all queries.

# SQL Queries

##### Table Setup (DDL)

```sql
CREATE TABLE cd.members (
	memid INT
	surname VARCHAR(200)
	firstname VARCHAR(200)
	address VARCHAR(300)
	zipcode INT
	telephone VARCHAR(20)
	recommendedby INT
	joindate TIMESTAMP 
PRIMARY KEY (memid) 
FOREIGN KEY (recommendedby)REFERENCES cd.members(memid)
);

CREATE TABLE cd.bookings (
	facid INT
	memid INT
	starttime TIMESTAMP
	slots INT
PRIMARY KEY (facid)
FOREIGN KEY (memid)REFERENCES cd.members(memid)
);

CREATE TABLE cd.facilities (
	facid INT
	name VARCHAR(100)
	membercost FLOAT
	guestcost FLOAT
	initialoutlay FLOAT
	monthlymaintenance FLOAT
FOREIGN KEY (facid)REFERENCES cd.bookings(facid)
);
```

#### Modifying Data

##### Question 1: The club is adding a new facility - a spa. We need to add it into the facilities table. Use the following values: facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.

```sql
INSERT INTO cd.facilities
VALUES (9, 'Spa', 20, 30, 100000, 800);

```
##### Question 2: Let's try adding the spa to the facilities table again. This time, though, we want to automatically generate the value for the next facid, rather than specifying it as a constant. Use the following values for everything else: Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.
```sql
INSERT INTO cd.facilities(facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (
  (SELECT max(facid) FROM cd.facilities) +1,
	'Spa', 20, 30, 100000, 800
	)
```

##### Question 3: We made a mistake when entering the data for the second tennis court. The initial outlay was 10000 rather than 8000: you need to alter the data to fix the error.

```sql
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE name LIKE 'Tennis Court 2';
```

##### Question 4: We want to alter the price of the second tennis court so that it costs 10% more than the first one. Try to do this without using constant values for the prices, so that we can reuse the statement if we want to.

```sql
UPDATE cd.facilities
SET membercost = (SELECT membercost FROM cd.facilities WHERE facid = 0)*1.1,
    guestcost = (SELECT guestcost FROM cd.facilities WHERE facid = 0)*1.1
WHERE facid = 1;
```

##### Question 5: As part of a clearout of our database, we want to delete all bookings from the cd.bookings table. How can we accomplish this?

```sql
TRUNCATE cd.bookings;
```

##### Question 6: We want to remove member 37, who has never made a booking, from our database. How can we achieve that?

```sql
DELETE FROM cd.members
WHERE memid = 37;
```

#### BASICS

##### Question 1: How can you produce a list of facilities that charge a fee to members, and that fee is less than 1/50th of the monthly maintenance cost? Return the facid, facility name, member cost, and monthly maintenance of the facilities in question.
```sql
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0
	AND membercost < (monthlymaintenance/50);
```

##### Question 2: How can you produce a list of all facilities with the word 'Tennis' in their name?
```sql
SELECT *
FROM cd.facilities
WHERE name LIKE '%Tennis%';
```

##### Question 3: How can you retrieve the details of facilities with ID 1 and 5? Try to do it without using the OR operator.
```sql
SELECT *
FROM cd.facilities
WHERE facid IN (1,5);
```

##### Question 4: How can you produce a list of members who joined after the start of September 2012? Return the memid, surname, firstname, and joindate of the members in question.
```sql 
SELECT memid, surname, firstname, joindate
FROM cd.members
WHERE joindate >= '2012-09-01';
```

##### Question 5: You, for some reason, want a combined list of all surnames and all facility names. Produce that list!
```sql 
SELECT surname FROM cd.members
UNION 
SELECT name FROM cd.facilities;
```

#### JOIN

##### Question 1: How can you produce a list of the start times for bookings by members named 'David Farrell'?
```sql
SELECT starttime
FROM cd.members mems JOIN cd.bookings books ON mems.memid = books.memid
WHERE firstname = 'David'
  AND surname = 'Farrell';

--Option without JOIN:
SELECT starttime 
FROM cd.bookings
WHERE memid IN (SELECT memid FROM cd.members WHERE firstname = 'David' AND surname = 'Farrell');
```

##### Question 2: How can you produce a list of the start times for bookings for tennis courts, for the date '2012-09-21'? Return a list of start time and facility name pairings, ordered by the time.
```sql 
SELECT book.starttime, fac.name
FROM cd.bookings book JOIN cd.facilities fac ON book.facid = fac.facid
WHERE name LIKE 'Tennis Court%'
AND starttime BETWEEN '2012-09-21 00:00:00' AND '2012-09-21 23:59:59'
ORDER BY book.starttime;
```

##### Question 3: How can you output a list of all members, including the individual who recommended them (if any)? Ensure that results are ordered by (surname, firstname).
```sql 
SELECT M1.firstname AS memfname, M1.surname AS memsname, M2.firstname AS recfname, M2.surname AS recsname
FROM cd.members M1 LEFT JOIN cd.members M2 ON M1.recommendedby = M2.memid
ORDER BY M1.surname, M1.firstname;
```

##### Question 4: How can you output a list of all members who have recommended another member? Ensure that there are no duplicates in the list, and that results are ordered by (surname, firstname).
```sql 
SELECT DISTINCT M1.firstname AS firstname, M1.surname AS surname
FROM cd.members M1 JOIN cd.members M2 ON M1.memid = M2.recommendedby
ORDER BY surname, firstname;
```

##### Question 5: How can you output a list of all members, including the individual who recommended them (if any), without using any joins? Ensure that there are no duplicates in the list, and that each firstname + surname pairing is formatted as a column and ordered.
```sql 
SELECT DISTINCT CONCAT(firstname, ' ', surname) AS member, 
	(SELECT CONCAT(firstname, ' ', surname) FROM cd.members recommender WHERE recommender.memid = member.recommendedby) AS recommender
FROM cd.members member
ORDER BY member;
```

#### AGGREGATION

##### Question 1: Produce a count of the number of recommendations each member has made. Order by member ID.
```sql 
SELECT recommendedby, count(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;
```

##### Question 2: Produce a list of the total number of slots booked per facility. For now, just produce an output table consisting of facility id and slots, sorted by facility id.
```sql 
SELECT facid, sum(slots) AS "Total Slots"
FROM cd.bookings
GROUP BY facid
ORDER BY facid;
```

#### Question 3: Produce a list of the total number of slots booked per facility in the month of September 2012. Produce an output table consisting of facility id and slots, sorted by the number of slots.
```sql
SELECT facid, sum(slots) AS "Total Slots"
FROM cd.bookings
WHERE starttime BETWEEN '09-01-12' AND '10-01-12'
GROUP BY facid
ORDER BY 2;
```

##### Question 4: Produce a list of the total number of slots booked per facility per month in the year of 2012. Produce an output table consisting of facility id and slots, sorted by the id and month.
```sql
SELECT facid, EXTRACT(month FROM starttime) AS month, sum(slots) AS "Total Slots"
FROM cd.bookings
WHERE EXTRACT(year FROM starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;
```

##### Question 5: Find the total number of members (including guests) who have made at least one booking.
```sql 
SELECT count(DISTINCT memid)
FROM cd.bookings;
```

##### Question 6: Produce a list of each member name, id, and their first booking after September 1st 2012. Order by member ID.
```sql
SELECT surname, firstname, mems.memid, min(starttime) AS first_booking
FROM cd.members mems JOIN cd.bookings books ON mems.memid = books.memid
WHERE starttime >= '09-01-12'
GROUP BY mems.memid
ORDER BY mems.memid;
```

##### Question 7: Produce a list of member names, with each row containing the total member count. Order by join date, and include guest members.
```sql 
SELECT count(*) OVER() AS count, firstname, surname
FROM cd.members
ORDER BY joindate;
```

##### Question 8: Produce a monotonically increasing numbered list of members (including guests), ordered by their date of joining. Remember that member IDs are not guaranteed to be sequential.

```sql 
SELECT row_number() OVER (ORDER BY joindate), firstname, surname
FROM cd.members;
```

##### Question 9: Output the facility id that has the highest number of slots booked. Ensure that in the event of a tie, all tieing results get output.
```sql
SELECT facid, total
FROM (
	SELECT facid, sum(slots) AS total, rank() OVER (ORDER BY sum(slots) desc) AS rank 
	FROM cd.bookings 
	GROUP BY facid
) as ranked
WHERE rank = 1;
```

#### STRING

##### Question 1: Output the names of all members, formatted as 'Surname, Firstname'
```sql 
SELECT CONCAT(surname, ', ', firstname)
FROM cd.members;
```

##### Question 2: You've noticed that the club's member table has telephone numbers with very inconsistent formatting. You'd like to find all the telephone numbers that contain parentheses, returning the member ID and telephone number sorted by member ID.
```sql 
SELECT memid, telephone
FROM cd.members
WHERE telephone LIKE '%(%';
```

##### Question 3: You'd like to produce a count of how many members you have whose surname starts with each letter of the alphabet. Sort by the letter, and don't worry about printing out a letter if the count is 0.
```sql 
SELECT SUBSTRING(surname, 1, 1) AS letter, count(*)
FROM cd.members
GROUP BY letter
ORDER BY letter;
```



