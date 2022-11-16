SELECT DISTINCT A.Author_ID, First_Name, Last_Name
FROM Authors A, Books B
WHERE B.Author_ID = A.Author_ID
AND B.Subject_ID IN
(SELECT DISTINCT B1.Subject_ID
FROM Books B1, Authors A1
WHERE B1.Author_ID = A1.Author_ID
AND A1.First_Name = 'J. K.'
AND A1.Last_Name = 'Rowling')
GROUP BY A.Author_ID, First_Name, Last_Name
HAVING COUNT(*) >=
(SELECT COUNT(DISTINCT S0.Subject_ID)
FROM Subjects S0, Books B0, Authors A0
WHERE S0.Subject_ID = B0.Subject_ID
AND B0.Author_ID = A0.Author_ID
AND A0.First_Name = 'J. K.'
AND A0.Last_Name = 'Rowling')
ORDER BY Last_Name ASC, A.Author_ID DESC;

