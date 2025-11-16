const http = require('http');

// API测试函数
function testAPI(method, path, data = null) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'localhost',
      port: 3001,
      path: path,
      method: method,
      headers: {
        'Content-Type': 'application/json',
      }
    };

    const req = http.request(options, (res) => {
      let body = '';
      res.on('data', (chunk) => {
        body += chunk;
      });
      res.on('end', () => {
        resolve({
          statusCode: res.statusCode,
          headers: res.headers,
          body: body
        });
      });
    });

    req.on('error', (err) => {
      reject(err);
    });

    if (data) {
      req.write(JSON.stringify(data));
    }
    req.end();
  });
}

// 测试用例
async function runTests() {
  console.log('🧪 开始API代理测试...\n');

  const tests = [
    {
      name: '登录接口测试',
      method: 'POST',
      path: '/api/user/login',
      data: {
        username: 'admin',
        password: 'admin123'
      }
    },
    {
      name: '获取用户信息',
      method: 'GET',
      path: '/api/user/info',
      data: null
    },
    {
      name: '获取统计数据',
      method: 'GET',
      path: '/api/statistics/overall',
      data: null
    },
    {
      name: '健康检查',
      method: 'GET',
      path: '/api/health',
      data: null
    }
  ];

  for (const test of tests) {
    console.log(`🔍 测试: ${test.name}`);
    try {
      const result = await testAPI(test.method, test.path, test.data);
      console.log(`   状态码: ${result.statusCode}`);

      if (result.statusCode === 502) {
        console.log('   ⚠️  后端服务未启动');
      } else if (result.statusCode >= 200 && result.statusCode < 300) {
        console.log('   ✅ 成功');
        try {
          const jsonData = JSON.parse(result.body);
          console.log('   响应:', JSON.stringify(jsonData, null, 2).substring(0, 200) + '...');
        } catch (e) {
          console.log('   响应:', result.body.substring(0, 100) + '...');
        }
      } else {
        console.log(`   ❌ 失败`);
        console.log('   响应:', result.body.substring(0, 100) + '...');
      }
    } catch (error) {
      console.log(`   ❌ 错误: ${error.message}`);
    }
    console.log(''); // 空行
  }

  console.log('🏁 API测试完成');
  console.log('\n💡 提示:');
  console.log('- 如果看到 "502 Bad Gateway"，说明后端服务未启动');
  console.log('- 需要先启动后端服务: cd backend && mvn spring-boot:run');
  console.log('- 或者使用开发模式后端: cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev');
}

// 运行测试
runTests().catch(console.error);