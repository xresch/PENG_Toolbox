
/********************************************
 * Select Permissions for User
 ********************************************/
SELECT P.*
FROM CFW_PERMISSION P
JOIN CFW_GROUP_PERMISSION_MAP AS GP ON GP.FK_ID_PERMISSION = P.PK_ID
JOIN CFW_USER_GROUP_MAP AS UG ON UG.FK_ID_GROUP = GP.FK_ID_GROUP
WHERE UG.FK_ID_USER = 3;