const http = require('http');

const PORT = 8081;

// 模拟数据
const mockUsers = {
  admin: {
    id: 1,
    username: 'admin',
    password: 'admin123', // 简化演示，实际应该加密
    name: '系统管理员',
    role: 'ADMIN',
    email: 'admin@dormitory.com'
  }
};

const mockStatistics = {
  studentStatistics: {
    totalStudents: 1200,
    checkedInStudents: 1150,
    pendingStudents: 30,
    applyingStudents: 20,
    checkedOutStudents: 0,
    maleStudents: 800,
    femaleStudents: 400,
    gradeDistribution: {
      '2021': 300,
      '2022': 350,
      '2023': 400,
      '2024': 150
    }
  },
  dormitoryStatistics: {
    totalDormitories: 20,
    totalRooms: 400,
    occupiedRooms: 380,
    availableRooms: 20,
    totalBeds: 2400,
    occupiedBeds: 2300,
    availableBeds: 100,
    buildings: [
      { id: 1, name: 'A栋', floors: 6, rooms: 120, occupied: 115 },
      { id: 2, name: 'B栋', floors: 6, rooms: 120, occupied: 118 },
      { id: 3, name: 'C栋', floors: 6, rooms: 80, occupied: 77 },
      { id: 4, name: 'D栋', floors: 6, rooms: 80, occupied: 70 }
    ]
  },
  feeStatistics: {
    totalFees: 2880000,
    paidFees: 2500000,
    unpaidFees: 380000,
    monthlyRevenue: 240000
  },
  visitorStatistics: {
    totalVisitors: 156,
    approvedVisitors: 142,
    pendingVisitors: 8,
    rejectedVisitors: 6,
    todayVisitors: 5
  }
};

const mockFees = [
  {
    id: 1,
    feeType: '住宿费',
    feeName: '2024年春季住宿费',
    description: '2024年春季学期住宿费用',
    amount: 1200.00,
    billingCycle: '学期',
    status: 'PAID',
    dueDate: '2024-03-01',
    paidDate: '2024-02-28',
    studentId: 1,
    studentName: '张三',
    studentNo: '2021001001',
    roomId: 101,
    roomNo: 'A101',
    buildingId: 1,
    buildingName: 'A栋'
  },
  {
    id: 2,
    feeType: '水电费',
    feeName: '3月水电费',
    description: '2024年3月水电费用',
    amount: 85.50,
    billingCycle: '月度',
    status: 'UNPAID',
    dueDate: '2024-04-05',
    paidDate: null,
    studentId: 1,
    studentName: '张三',
    studentNo: '2021001001',
    roomId: 101,
    roomNo: 'A101',
    buildingId: 1,
    buildingName: 'A栋'
  }
];

// 设置CORS头
function setCORSHeaders(res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
}

const server = http.createServer((req, res) => {
  // 设置CORS头
  setCORSHeaders(res);

  // 处理OPTIONS预检请求
  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  const url = req.url;
  console.log(`📝 Mock API: ${req.method} ${url}`);

  // 路由处理
  if (url.startsWith('/api/user/login') && req.method === 'POST') {
    // 登录接口
    let body = '';
    req.on('data', chunk => {
      body += chunk.toString();
    });
    req.on('end', () => {
      try {
        const { username, password } = JSON.parse(body);
        const user = mockUsers[username];

        if (user && user.password === password) {
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({
            code: 200,
            message: '登录成功',
            data: {
              token: 'mock-jwt-token-' + Date.now(),
              user: {
                id: user.id,
                username: user.username,
                name: user.name,
                role: user.role,
                email: user.email
              }
            }
          }));
        } else {
          res.writeHead(401, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({
            code: 401,
            message: '用户名或密码错误'
          }));
        }
      } catch (error) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
          code: 400,
          message: '请求格式错误'
        }));
      }
    });
  } else if (url.startsWith('/api/user/info') && req.method === 'GET') {
    // 获取用户信息
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      code: 200,
      message: '成功',
      data: {
        id: 1,
        username: 'admin',
        name: '系统管理员',
        role: 'ADMIN',
        email: 'admin@dormitory.com',
        avatar: null
      }
    }));
  } else if (url.startsWith('/api/user/logout') && req.method === 'POST') {
    // 登出接口
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      code: 200,
      message: '登出成功'
    }));
  } else if (url.startsWith('/api/statistics/overall') && req.method === 'GET') {
    // 获取统计数据
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      code: 200,
      message: '成功',
      data: mockStatistics
    }));
  } else if (url.startsWith('/api/fee/list') && req.method === 'GET') {
    // 获取费用列表
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      code: 200,
      message: '成功',
      data: {
        records: mockFees,
        total: mockFees.length,
        size: 10,
        current: 1
      }
    }));
  } else if (url.startsWith('/api/health') && req.method === 'GET') {
    // 健康检查
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      code: 200,
      message: '服务正常',
      data: {
        status: 'UP',
        timestamp: new Date().toISOString(),
        version: '1.0.0'
      }
    }));
  } else {
    // 404 - 其他路径
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      code: 404,
      message: `接口不存在: ${url}`,
      data: null
    }));
  }
});

server.listen(PORT, () => {
  console.log(`
🚀 模拟后端API服务器已启动
========================================
📡 服务地址: http://localhost:${PORT}
🛠️  状态: 运行中
📋 支持的接口:
   - POST /api/user/login        - 用户登录
   - GET  /api/user/info         - 用户信息
   - POST /api/user/logout       - 用户登出
   - GET  /api/statistics/overall - 统计数据
   - GET  /api/fee/list          - 费用列表
   - GET  /api/health            - 健康检查

🧪 测试账号:
   用户名: admin
   密码: admin123
========================================
按 Ctrl+C 停止服务器
  `);
});

process.on('SIGINT', () => {
  console.log('\n🛑 正在关闭Mock API服务器...');
  server.close(() => {
    console.log('✅ Mock API服务器已关闭');
    process.exit(0);
  });
});