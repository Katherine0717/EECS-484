-- Exact one book
SELECT Books.Author_ID
FROM Books
GROUP BY Books.Author_ID
HAVING COUNT(*) = 1
ORDER BY Books.Author_ID ASC;