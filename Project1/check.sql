-- check user information wrong
SELECT * FROM project1.PUBLIC_USER_INFORMATION
MINUS
SELECT * FROM VIEW_USER_INFORMATION;

SELECT * FROM VIEW_USER_INFORMATION
MINUS
SELECT * FROM project1.PUBLIC_USER_INFORMATION;

-- -- check friends correct
-- SELECT LEAST(USER1_ID, USER2_ID), GREATEST(USER1_ID, USER2_ID)
-- FROM project1.PUBLIC_ARE_FRIENDS
-- MINUS
-- SELECT LEAST(USER1_ID, USER2_ID), GREATEST(USER1_ID, USER2_ID)
-- FROM VIEW_ARE_FRIENDS;

-- SELECT LEAST(USER1_ID, USER2_ID), GREATEST(USER1_ID, USER2_ID)
-- FROM VIEW_ARE_FRIENDS
-- MINUS
-- SELECT LEAST(USER1_ID, USER2_ID), GREATEST(USER1_ID, USER2_ID)
-- FROM project1.PUBLIC_ARE_FRIENDS;


-- -- check photo correct
-- SELECT * FROM project1.PUBLIC_PHOTO_INFORMATION
-- MINUS
-- SELECT * FROM VIEW_PHOTO_INFORMATION;

-- SELECT * FROM VIEW_PHOTO_INFORMATION
-- MINUS
-- SELECT * FROM project1.PUBLIC_PHOTO_INFORMATION;


-- -- check event correct
-- SELECT * FROM project1.PUBLIC_EVENT_INFORMATION
-- MINUS
-- SELECT * FROM VIEW_EVENT_INFORMATION;

-- SELECT * FROM VIEW_EVENT_INFORMATION
-- MINUS
-- SELECT * FROM project1.PUBLIC_EVENT_INFORMATION;

-- -- check tag WRONG
-- SELECT * FROM project1.PUBLIC_TAG_INFORMATION
-- MINUS
-- SELECT * FROM VIEW_TAG_INFORMATION;

-- SELECT * FROM VIEW_TAG_INFORMATION
-- MINUS
-- SELECT * FROM project1.PUBLIC_TAG_INFORMATION;
