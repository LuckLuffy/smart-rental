import subprocess
import os
import sys
import shutil

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
FRONTEND_DIR = os.path.join(BASE_DIR, "frontend")

# 自动查找 npm 完整路径
npm_cmd = shutil.which("npm")
if npm_cmd is None:
    npm_cmd = shutil.which("npm.cmd")
if npm_cmd is None:
    print("错误: 找不到 npm，请确保已安装 Node.js")
    sys.exit(1)

print("=" * 50)
print("  智慧租房 - 前端启动中...")
print("  地址: http://localhost:3000")
print("=" * 50)
print()

os.chdir(FRONTEND_DIR)
subprocess.run([npm_cmd, "run", "dev"], check=False)
