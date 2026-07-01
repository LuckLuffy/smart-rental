import subprocess
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SQL_FILE = os.path.join(BASE_DIR, "backend", "src", "main", "resources", "db", "init.sql")

print("步骤1: 强制删除旧数据库...")
r1 = subprocess.run(
    'mysql -u root -p123456 --default-character-set=utf8mb4 -e "DROP DATABASE IF EXISTS smart_rental; CREATE DATABASE smart_rental DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"',
    shell=True, capture_output=True, text=True
)
if r1.returncode != 0:
    print("错误:", r1.stderr)
    exit(1)
print("  旧库已清除，新库已创建")

print("步骤2: 导入表结构和测试数据...")
with open(SQL_FILE, 'r', encoding='utf-8') as f:
    sql_content = f.read()

r2 = subprocess.run(
    ['mysql', '-u', 'root', '-p123456', '--default-character-set=utf8mb4', 'smart_rental'],
    input=sql_content, capture_output=True, text=True, encoding='utf-8'
)
if r2.returncode != 0:
    stderr = r2.stderr
    for line in stderr.split('\n'):
        if 'Warning' not in line and line.strip():
            print("  ", line.strip())
    exit(1)
print("数据库初始化完成！")
