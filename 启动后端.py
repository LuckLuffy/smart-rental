import subprocess
import os
import sys

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
BACKEND_DIR = os.path.join(BASE_DIR, "backend")

# Maven 路径
mvn_cmd = r"D:\wyq\idea\IntelliJ IDEA 2025.3\plugins\maven\lib\maven3\bin\mvn.cmd"

print("=" * 50)
print("  智慧租房 - 后端启动中...")
print("  地址: http://localhost:8080")
print("=" * 50)
print()

os.chdir(BACKEND_DIR)
subprocess.run([mvn_cmd, "spring-boot:run"], check=False)
