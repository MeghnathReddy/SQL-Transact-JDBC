DROP TABLE IF EXISTS Enrolled;
DROP TABLE IF EXISTS Class;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Faculty;

CREATE TABLE Student (
    snum INT  PRIMARY KEY ,
    sname VARCHAR(64) NOT NULL,
    major VARCHAR(64) NOT NULL,
    slevel VARCHAR(64) NOT NULL,
    age INT NOT NULL

);

CREATE TABLE Faculty(
    fid INT PRIMARY KEY,
    fname VARCHAR(64) NOT NULL,
    deptid INT NOT NULL,
    salary REAL NOT NULL,
);

CREATE TABLE Class(
    cname VARCHAR(64) PRIMARY KEY,
    meets_at VARCHAR(64) NOT NULL,
    room VARCHAR(64) NOT NULL,
    fid INT NOT NULL,
    FOREIGN KEY (fid) REFERENCES Faculty(fid)
);

CREATE TABLE Enrolled(
    snum INT,
    cname VARCHAR(64),
    FOREIGN KEY (snum) REFERENCES Student(snum),
    FOREIGN KEY (cname) REFERENCES Class(cname),
    CONSTRAINT PK_Enrolled PRIMARY KEY (snum,cname)
);


-- Insert some valid records
--Student records
INSERT INTO Student VALUES
(1,'Adams','History','FR',18),
(2,'Bethany','History','FR',20),
(3,'Adams','CS','SF',20),
(4,'Codd','CS','SF',22),
(5,'Daniels','ECE','JR',22),
(6,'Daniels','CS','JR',24),
(7,'Gordon','ECE','SR',24),
(8,'Smith','Physics','SR',26);

--Faculty records
INSERT INTO Faculty VALUES
(101,'Johnson',10,55000),
(102,'Lynn',10,65000),
(103,'Lynn',12,30000),
(104,'Black',11,32000);

--Class records
INSERT INTO Class VALUES
('Intro to Java','W 13:30','R128',102),
('CS 4513','F 12:00','K53',102),
('Intro to Pascal','F 09:00','S217',102),
('Data structures','W 13:30','S217',103),
('Advanced Java','M 15:30','R128',103),
('Data Networks','M 15:30','S217',101),
('Operating Systems','F 09:00','K53',103),
('Intro to Compilers','M 14:30','K53',101),
('Computer Architecture','W 08:00','R128',101),
('Software engineering','W 10:00','R128',104);

--Enrolled records
INSERT INTO Enrolled VALUES
(4,'Data Networks'),
(5,'Data Networks'),
(6,'Intro to Compilers'),
(4,'Intro to Compilers'),
(5,'Intro to Compilers'),
(1,'Intro to Compilers'),
(2,'Intro to Compilers'),
(3,'Intro to Compilers'),
(4,'Advanced Java');

------Problem 1.1 checking for violation of constraint
--a)Uniqueness
INSERT INTO Student VALUES
(1,'VK','History','FR',18)

--b)Not null
INSERT INTO Faculty VALUES
( NULL,'Pearson',10,55000)

---c)Foriegn key violation
--Insert
INSERT INTO Enrolled VALUES
(12,'Data Networks')

--DELETE 
DELETE FROM Student WHERE snum=4

---UPDATE
UPDATE Student SET snum=30 WHERE snum=2
UPDATE Enrolled SET snum=45 WHERE snum=2

--d) Retrieval 
SELECT sname FROM Student WHERE sname=4


-- problem 1.2 queries 
-- Index creation
CREATE index slevel_idx ON Student(slevel)

--Dropping the index 
DROP index student.slevel_idx

-- query1 executed to check for the execution time differences

SELECT  sname FROM Student WHERE snum in (SELECT snum FROM Enrolled WHERE cname in (SELECT cname FROM Class WHERE fid in (SELECT fid  FROM Faculty WHERE fname = 'johnson'))) AND slevel = 'JR'

-- query2 executed to check for the execution time differences

SELECT slevel ,AVG(age) as average_age FROM Student GROUP BY slevel


-- query3 executed to check for the execution time differences

DELETE FROM Enrolled WHERE snum in (SELECT snum FROM Student WHERE slevel = 'SR' )


-- query4 executed to check for the execution time differences

DELETE FROM Student WHERE slevel = 'SR'

----------------------Problem 2
--FOR OPTION-1
DROP PROCEDURE IF EXISTS insertintofaculty 
GO
CREATE PROCEDURE insertintofaculty
--Parameters to be taken as input are declared
@facid INT ,
@facname VARCHAR(64) ,
@facdept INT 
AS
BEGIN
        DECLARE @av_salary FLOAT; --DECLARATION
        SET @av_salary = (SELECT AVG(salary) FROM Faculty GROUP BY deptid HAVING deptid = @facdept);
        ---Looping statements to satisfy given conditions
        IF @av_salary > 50000
            INSERT INTO Faculty VALUES(@facid,@facname,@facdept,0.9*@av_salary);
        IF @av_salary < 30000
            
            INSERT INTO Faculty VALUES(@facid,@facname,@facdept,@av_salary);
        ELSE    
            INSERT INTO Faculty VALUES(@facid,@facname,@facdept,0.8*@av_salary);
END
GO

---FOR OPTION 2
DROP PROCEDURE IF EXISTS insertintofaculty2
GO
CREATE PROCEDURE insertintofaculty2
--Parameters to be taken as input are declared
@facid2 INT,
@facname2 VARCHAR(64),
@facdeptid2 INT,
@exclude_deptid INT
AS
BEGIN
        DECLARE @av_salary2 FLOAT; --DECLARATION
        SET @av_salary2 = (SELECT AVG(salary) FROM Faculty where deptid != @exclude_deptid); --this will exclude the deptartment
        INSERT INTO Faculty VALUES (@facid2 , @facname2, @facdeptid2, @av_salary2);

END
GO
