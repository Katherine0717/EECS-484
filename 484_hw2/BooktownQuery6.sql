SELECT DISTINCT B.Title, E.Publication_Date, A.Author_ID, A.First_Name, A.Last_Name
FROM Editions E, Books B, Authors A
WHERE E.Book_ID = B.Book_ID
AND A.Author_ID = B.Author_ID
AND A.Author_ID IN
(SELECT Authors.Author_ID
FROM Editions, Books, Authors
WHERE Editions.Book_ID = Books.Book_ID
AND Books.Author_ID = Authors.Author_ID
AND Editions.Publication_Date >= '2003-01-01'
AND Editions.Publication_Date <= '2008-12-31')
ORDER BY Author_ID ASC, Title ASC, Publication_Date DESC;