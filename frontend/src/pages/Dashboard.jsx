import { useNavigate } from 'react-router-dom';
import { 
  Users, HeartPulse, DollarSign, Pill, Building2, 
  Activity, ClipboardList, Settings 
} from 'lucide-react';

export default function Dashboard() {
  const navigate = useNavigate();
  const role = localStorage.getItem('role');
  const name = localStorage.getItem('fullName');

  // 1. Define all modules and their allowed roles
  const modules = [
    {
      title: "Staff Management",
      description: "Manage hospital staff, roles, and ranks.",
      icon: Users,
      route: "/staff",
      allowedRoles: ["Super Admin", "Hospital Admin"]
    },
    {
      title: "Patient Management",
      description: "Register patients, view records, and consultations.",
      icon: HeartPulse,
      route: "/patients", // Placeholder for next stage
      allowedRoles: [ "Hospital Admin", "Doctor", "Nurse"]
    },
    {
      title: "Finance & Billing",
      description: "Invoices, payments, and staff payroll.",
      icon: DollarSign,
      route: "/finance", // Placeholder
      allowedRoles: [ "Hospital Admin"]
    },
    {
      title: "Pharmacy & Inventory",
      description: "Manage drugs, stock levels, and dispensing.",
      icon: Pill,
      route: "/pharmacy", // Placeholder
      allowedRoles: [ "Hospital Admin", "Pharmacist"]
    },
    {
      title: "Laboratory",
      description: "Manage lab requests and test results.",
      icon: Activity,
      route: "/laboratory", // Placeholder
      allowedRoles: [ "Hospital Admin", "Lab Technologist"]
    },
    {
      title: "Hospital Management",
      description: "Manage registered hospitals and SaaS settings.",
      icon: Building2,
      route: "/hospitals", // Placeholder for Super Admin
      allowedRoles: ["Super Admin"] // ONLY Super Admin sees this
    }
  ];

  // 2. Filter modules based on the user's role
  const accessibleModules = modules.filter(module => 
    module.allowedRoles.includes(role)
  );

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-2xl p-8 text-white shadow-lg">
        <h2 className="text-3xl font-bold mb-2">Welcome back, {name}!</h2>
        <p className="text-blue-100 text-lg">
          {role === 'Super Admin' 
            ? 'Manage your SaaS platform and registered hospitals.' 
            : 'Manage your hospital operations efficiently.'}
        </p>
      </div>

      {/* Dynamic Module Cards Grid */}
      <div>
        <h3 className="text-xl font-semibold text-gray-800 mb-4">Your Modules</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {accessibleModules.map((module, index) => {
            const Icon = module.icon;
            return (
              <div 
                key={index}
                onClick={() => navigate(module.route)}
                className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 hover:shadow-md hover:border-primary transition cursor-pointer group"
              >
                <div className="bg-blue-50 p-3 rounded-lg w-fit mb-4 group-hover:bg-primary transition">
                  <Icon className="h-8 w-8 text-primary group-hover:text-white transition" />
                </div>
                <h4 className="text-lg font-semibold text-gray-800 mb-1">{module.title}</h4>
                <p className="text-gray-500 text-sm">{module.description}</p>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}