SELECT DISTINCT Publishers.Publisher_ID, Publishers.Name
FROM Publishers, Editions, Books B0, Authors A0
WHERE Publishers.Publisher_ID = Editions.Publisher_ID
AND Editions.Book_ID = B0.Book_ID
AND B0.Author_ID = A0.Author_ID
AND A0.Author_ID IN
(SELECT A1.Author_ID
FROM Authors A1, Books B1
WHERE A1.Author_ID = B1.Author_ID
GROUP BY B1.Author_ID
HAVING COUNT(*) = 3)
ORDER BY Publisher_ID DESC;
