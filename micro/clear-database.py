import mysql.connector

# 简单配置
conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='123',
    database='springboot'
)

cur = conn.cursor()

# 一条语句搞定：禁用外键 + 删除所有表
cur.execute("SET FOREIGN_KEY_CHECKS = 0;")
cur.execute("SHOW TABLES;")
for (table,) in cur.fetchall():
    cur.execute(f"DROP TABLE `{table}`;")
cur.execute("SET FOREIGN_KEY_CHECKS = 1;")

conn.commit()
cur.close()
conn.close()

print("All tables dropped.")
