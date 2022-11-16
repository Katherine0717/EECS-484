SELECT A1.First_Name, A1.Last_Name
From Authors A1
WHERE A1.Author_ID IN
((SELECT A0.Author_ID
FROM Authors A0, Books, Subjects
WHERE Books.Author_ID = A0.Author_ID
AND Books.Subject_ID = Subjects.Subject_ID
AND Subjects.Subject = 'Children/YA')
INTERSECT
(SELECT A2.Author_ID
FROM Authors A2, Books, Subjects
WHERE Books.Author_ID = A2.Author_ID
AND Books.Subject_ID = Subjects.Subject_ID
AND Subjects.Subject = 'Fiction'))
ORDER BY First_Name ASC, Last_Name ASC;
